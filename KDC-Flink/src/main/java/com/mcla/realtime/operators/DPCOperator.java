package com.mcla.realtime.operators;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.index.NDIndex;
import ai.djl.ndarray.types.Shape;
import ai.djl.util.cuda.CudaUtils;
import com.mcla.realtime.bean.ClusterReading;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.lang.management.MemoryUsage;

public class DPCOperator {
    // 算法整体框架
    public long[] DPC(NDArray nd) {
        Device d = Device.gpu(0);
        System.out.println("Device: " + d + ", id:" + d.getDeviceId() + ", type" + d.getDeviceType());
        MemoryUsage mem = CudaUtils.getGpuMemory(d);
        System.out.println("max memory:" + mem.getMax());
        System.out.println("Cuda version:" + CudaUtils.getCudaVersion());

        NDManager manager = NDManager.newBaseManager();
        // 计算距离矩阵
        NDArray dists = getDistanceMatrix(nd);
        // 计算dc
        NDArray dc = select_dc(dists);
        // 用高斯方程计算局部密度
        String method = "Gaussion";
        // 计算局部密度
        NDArray rho = get_density(dists, dc, method);
        // 计算密度距离
        NDArray deltas = get_deltas(dists, rho);
        // 获取聚类中心点
        NDArray centers = find_centers_K(rho, deltas);
        // 返回最大一个 （目前这样）
//        long centersmax = centers.get(new NDIndex("0")).toLongArray()[0];
        long[] centersmax = centers.toLongArray();
//            double deltasmax =deltas.get(new NDIndex("{}",centersmax)).toDoubleArray()[0];
//            double rhomax =rho.get(new NDIndex("{}",centersmax)).toDoubleArray()[0];
        return centersmax;
    }

    private NDArray getDistanceMatrix(NDArray nd) {
        // 计算数据点两两之间的距离
        NDManager manager = NDManager.newBaseManager();
        // 获取nd的维度数（n,d）
        Shape e = nd.getShape();
        // dists初始化为维度为（n,n）
        NDArray dists = manager.zeros(new Shape(e.size() / e.dimension(), e.size() / e.dimension()));
        // 求出每个点到其它点的距离
        for (int i = 0; i < dists.getShape().dimension(); i++) {
            for (int j = 0; j < dists.getShape().dimension(); j++) {
//                System.out.println(String.format("i: {%d}", i));
//                System.out.println(String.format("j: {%d}", j));
                NDArray vi = nd.get(new NDIndex("{}", i));
                NDArray vj = nd.get(new NDIndex("{}", j));
                dists.set(new NDIndex("{},{}", i, j), array -> {
                    array = ((vi.sub(vj)).dot(vi.sub(vj))).sqrt();
                    return array;
                });
            }
        }
        return dists;
    }

    // 找到密度计算的阈值dc
    // 要求平均每个点周围距离小于dc的点的数目占总点数的1%-2%
    private NDArray select_dc(NDArray dists) {
        // 获取 dists的维度数（n,d）
        Shape e = dists.getShape();
        // 求出 n 值
        long N = e.get(0);
        //把 dists的形状改为一维 列数N * N
        NDArray tt = dists.reshape(N * N);
        // 定义筛选百分比
        double percent = 2.0;
        // 位置
        int position = (int) (N * (N - 1) * percent / 100);
        // 返回dc值
        return (tt.sort()).get(new NDIndex("{}", position + N));
    }

    private NDArray get_density(NDArray dists, NDArray dc, String method) {
        // 获取dists的维度数（n,d）
        Shape e = dists.getShape();
        // 求出 n 值
        long N = e.get(0);
        // 初始化rho数组
        NDManager manager = NDManager.newBaseManager();
        NDArray rho = manager.zeros(new Shape(N));
        for (int i = 0; i < N; i++) {
            // 如果没有指定用什么方法，默认方法
            if (method == null) {

                // 筛选出 （dists[i, :] < dc）条件下的行
                NDArray g = dists.get(new NDIndex("{}", i));
                NDArray s = g.get(g.lte(dc)).get(new NDIndex("0"));
                // rho[i]为s的维度-1
                Shape c = s.getShape();
                long a = c.get(0) - 1;
                NDArray r = manager.create(a);
                rho.set(new NDIndex("{}", i), aa -> r);
            } else   // 使用高斯方程计算
            {
                // 没想让你们看懂
                NDArray t = ((dists.get(new NDIndex("{}", i)).div(dc)).pow(2).neg().exp()).sum().sub(1);
                rho.set(new NDIndex("{}", i), aa -> t);
            }

        }
        return rho;

    }

    private NDArray get_deltas(NDArray dists, NDArray rho) {
        // 获取 dists的维度数（n,d）
        Shape e = dists.getShape();
        // 求出 n 值
        long N = e.get(0);
        // 初始化deltas数组
        NDManager manager = NDManager.newBaseManager();
        NDArray deltas = manager.zeros(new Shape(N));
        NDArray index_rho = rho.argSort().flip(0);
        for (int i = 0; i < N; i++) {
            // 写出值
            long index = index_rho.get(new NDIndex("{}", i)).toLongArray()[0];
            // 对于密度最大的点
            if (i == 0)
                continue;
            // 对于其它的点
            // 找到密度比其它点大的序号
            NDArray index_higher_rho = index_rho.get(new NDIndex(":{}", i));
            // 获取这些点距离当前点的距离,并找最小值
            // 下面这段对应python语句为：deltas[index] = np.min(dists[index, index_higher_rho])
            // 看了懂算我输
            NDArray z = dists.get(new NDIndex("{}", index));
            long Z = index_higher_rho.size();
            for (int c = 0; c < Z; c++) {
                NDArray C = manager.zeros(new Shape(Z));
                int finalC = c;
                C.set(new NDIndex("{}", c), aa -> z.get(new NDIndex("{}", index_higher_rho.toLongArray()[finalC])));
                deltas.set(new NDIndex("{}", index), aa -> C.min());
            }
        }
        // 导入最大值
        long max = index_rho.get(new NDIndex("{}", 0)).toLongArray()[0];
        deltas.set(new NDIndex("{}", max), aa -> deltas.max());
        return deltas;
    }

    private NDArray find_centers_K(NDArray rho, NDArray deltas) {
        // 每个点都相乘
        NDArray rho_delta = rho.mul(deltas);
        // 从大到小排序返回下标NDArray数组
        NDArray centers = rho_delta.argSort().flip(0);
        // 返回最大的三个下标
//        return centers.get(new NDIndex(":3"));
        return centers;

    }

}

