import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Test {
    static List<List<Integer>> totalList = new LinkedList<>();
    static List<Integer> path = new LinkedList<>();
    public static void main(String[] args) {
        combine(4,2);
    }

    public static List<List<Integer>> combine(int n, int k) {
        getResult(1,k,n);
        return totalList;
    }
    public static void getResult(int start, int k, int n){
        if(k==0) {
            totalList.add(new ArrayList<Integer>(path)); // 这里必须要新建一个对象，不然所有列表里都是重复的引用
            path.clear();
            return;
        }
        for(int i=start;i<=n;i++){
            path.add(i);
            getResult(start+1,k-1,n);// 很显然，代码不是按顺序执行的
        }
    }

}
