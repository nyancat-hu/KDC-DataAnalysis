package com.mcla.realtime.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mcla.realtime.common.PravegaConstant;
import com.mcla.realtime.utils.PravegaUtils;
import io.pravega.client.stream.*;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BaseLogApp {
    private static final String TOPIC_START = "dwd_start_log";
    private static final String TOPIC_DISPLAY = "dwd_display_log";
    private static final String TOPIC_PAGE = "dwd_page_log";

    public static void main(String[] args) throws Exception {

        //TODO 1.初始化环境

        // 1.1 Pravega, initialize the parameter utility tool in order to retrieve input parameters
        ParameterTool params = ParameterTool.fromArgs(args);
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withDefaultScope(PravegaConstant.DEFAULT_SCOPE);

        // 1.2 create the Pravega input stream (if necessary)
        Stream stream = PravegaUtils.createStream(
                pravegaConfig,
                params.get(PravegaConstant.STREAM_PARAM, PravegaConstant.DEFAULT_STREAM));

        //1.3 创建Flink流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //1.4 设置并行度
        env.setParallelism(1);

        //1.3设置Checkpoint
        //每5000ms开始一次checkpoint，模式是EXACTLY_ONCE（默认）
        //env.enableCheckpointing(5000, CheckpointingMode.EXACTLY_ONCE);
        //env.getCheckpointConfig().setCheckpointTimeout(60000);
        //env.setStateBackend(new FsStateBackend("hdfs://hadoop202:8020/gmall/checkpoint/baselogApp"));

        //System.setProperty("HADOOP_USER_NAME","atguigu");

        //TODO 2.从Pavega中读取数据

        // create the Pravega source to read a stream of text
        FlinkPravegaReader<String> pravegaSource = FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(stream)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();
        DataStreamSource<String> pravegaDS = env.addSource(pravegaSource);

        //TODO 3.对读取到的数据格式进行转换（String->json）

        // 当然我们也可以通过process方法，将异常json数据写入侧输出流，假如后面要用到的话
        SingleOutputStreamOperator<JSONObject> jsonObjDS = pravegaDS.map(
                (MapFunction<String, JSONObject>) JSON::parseObject
        );

        /*
        TODO 4.保存每个 mid 的首次访问日期，每条进入该算子的访问记录，都会把 mid 对应的首次访问时间读取出来，只有首次访问时间不为空，则认为该
         访客是老访客，否则是新访客。同时如果是新访客且没有访问记录的话，会写入首次访问时间。和日志产生日志进行对比。如果状态不为空，并且状态日期
         和当前日期不相等，说明是老访客，如果is_new标记是1，那么对其状态进行修复
        */

        //4.1 根据mid对日志进行分组，这里的mid是设备ID，理论上不会重复
        //keyBy后，对每个mid进行并行处理
        KeyedStream<JSONObject, String> midKeyedDS = jsonObjDS.keyBy(
                data -> data.getJSONObject("common").getString("mid")
        );

        //4.2 新老方法状态修复   状态分为算子状态和键控状态，
        // 我们这里要记录某一个设备的访问，使用键控状态比较合适
        SingleOutputStreamOperator<JSONObject> jsonDSWithFlag = midKeyedDS.map(
                //RichMapFunction进行状态编程
                new RichMapFunction<JSONObject, JSONObject>() {
                    //定义该mid访问状态，在底层是一个Map结构，map的value就是ValueState对象
                    private ValueState<String> firstVisitDateState;
                    //定义日期格式化对象
                    private SimpleDateFormat sdf;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        //对状态以及日期格式进行初始化
                        firstVisitDateState = getRuntimeContext().getState(
                                new ValueStateDescriptor<String>("newMidDateState", String.class)
                        );
                        sdf = new SimpleDateFormat("yyyyMMdd");
                    }

                    @Override
                    public JSONObject map(JSONObject jsonObj) throws Exception {
                        //获取当前日志标记状态
                        String isNew = jsonObj.getJSONObject("common").getString("is_new");

                        //获取当前日志访问时间戳
                        Long ts = jsonObj.getLong("ts");

                        if ("1".equals(isNew)) {
                            //获取当前mid对象的状态
                            String stateDate = firstVisitDateState.value();
                            //对当前条日志的日期格式进行抓换
                            String curDate = sdf.format(new Date(ts));
                            //如果状态不为空，并且状态日期和当前日期不相等，说明是老访客
                            if (stateDate != null && stateDate.length() != 0) {
                                //判断是否为同一天数据
                                if (!stateDate.equals(curDate)) {
                                    isNew = "0";
                                    jsonObj.getJSONObject("common").put("is_new", isNew);
                                }
                            } else {
                                //如果还没记录设备的状态，将当前访问日志作为状态值
                                firstVisitDateState.update(curDate);
                            }
                        }
                        return jsonObj;
                    }
                }
        );

        //jsonDSWithFlag.print(">>>>>>>>>>>");

        //TODO 5 .分流  根据日志数据内容,将日志数据分为3类, 页面日志、启动日志和曝光日志。
        // 页面日志输出到主流,启动日志输出到启动侧输出流,曝光日志输出到曝光日志侧输出流
        // 侧输出流：1)接收迟到数据    2)分流

        //定义启动侧输出流标签
        OutputTag<String> startTag = new OutputTag<String>("start") {
        };
        //定义曝光侧输出流标签
        OutputTag<String> displayTag = new OutputTag<String>("display") {
        };

        SingleOutputStreamOperator<String> pageDS = jsonDSWithFlag.process(
                new ProcessFunction<JSONObject, String>() {
                    @Override
                    public void processElement(JSONObject jsonObj, Context ctx, Collector<String> out) throws Exception {
                        //获取启动日志标记
                        JSONObject startJsonObj = jsonObj.getJSONObject("start");
                        //将json格式转换为字符串，方便向侧输出流输出以及向kafka中写入
                        String dataStr = jsonObj.toString();

                        //判断是否为启动日志
                        if (startJsonObj != null && startJsonObj.size() > 0) {
                            //如果是启动日志，输出到启动侧输出流
                            ctx.output(startTag, dataStr);
                        } else {
                            //如果不是启动日志  说明是页面日志 ，输出到主流
                            out.collect(dataStr);

                            //如果不是启动日志，获取曝光日志标记（曝光日志中也携带了页面）
                            JSONArray displays = jsonObj.getJSONArray("displays");
                            //判断是否为曝光日志
                            if (displays != null && displays.size() > 0) {
                                //如果是曝光日志，遍历输出到侧输出流
                                for (int i = 0; i < displays.size(); i++) {
                                    //获取每一条曝光事件
                                    JSONObject displaysJsonObj = displays.getJSONObject(i);
                                    //获取页面id
                                    String pageId = jsonObj.getJSONObject("page").getString("page_id");
                                    //给每一条曝光事件加pageId
                                    displaysJsonObj.put("page_id", pageId);
                                    ctx.output(displayTag, displaysJsonObj.toString());
                                }
                            }

                        }
                    }
                }
        );

        //获取侧输出流
        DataStream<String> startDS = pageDS.getSideOutput(startTag);
        DataStream<String> displayDS = pageDS.getSideOutput(displayTag);

        //打印输出
        pageDS.print("page>>>>");
        startDS.print("start>>>>");
        displayDS.print("display>>>>");

//        //TODO 6.将不同流的数据写回到kafka的不同topic中
//        FlinkKafkaProducer<String> startSink = MyKafkaUtil.getKafkaSink(TOPIC_START);
//        startDS.addSink(startSink);
//
//        FlinkKafkaProducer<String> displaySink = MyKafkaUtil.getKafkaSink(TOPIC_DISPLAY);
//        displayDS.addSink(displaySink);
//
//        FlinkKafkaProducer<String> pageSink = MyKafkaUtil.getKafkaSink(TOPIC_PAGE);
//        pageDS.addSink(pageSink);

        //TODO 6.将结果输出

        // create an output sink to print to stdout for verification
        pravegaDS.print();

        env.execute();

    }
}
