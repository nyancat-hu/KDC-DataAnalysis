package com.mcla.realtime.app.ods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mcla.realtime.common.PravegaConstant;
import com.mcla.realtime.utils.MyKafkaUtil;
import com.mcla.realtime.utils.MyPravegaUtil;
import io.pravega.client.stream.Stream;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;


public class BaseLogApp {

    private static final String SCOPE_ITEM = "dwd_item_log";
    private static final String STREAM_ITEM = "base_item_app_stream";
    private static final String SCOPE_TILEENTITY = "dwd_tileentity_log";
    private static final String STREAM_TILEENTITY = "base_tileEntity_app_stream";
    private static final String SCOPE_ENTITY = "dwd_entity_log";
    private static final String STREAM_ENTITY = "base_entity_app_stream";
    private static final String SCOPE_STATE = "dwd_state_log";
    private static final String STREAM_STATE = "base_state_app_stream";


    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        //TODO 1.从Pravega中读取数据
        String scope = "ods_base_log";
        String stream = "base_log_app_stream";

        // 调用Pravega工具类，获取FlinkPravegaReader
        FlinkPravegaReader<String> pravegaSource = MyPravegaUtil.getPravegaReader(args, scope, stream);

        // 创建一个Pravega输入流，并将其转换为JSON
        DataStreamSource<String> jsonStrDS = env.addSource(pravegaSource);
        SingleOutputStreamOperator<JSONObject> jsonObjDS = jsonStrDS.map(JSON::parseObject);

        //TODO 2.对数据进行ETL   serverName 或者 world为空 ，将这样的数据过滤掉，但要保留系统日志
        SingleOutputStreamOperator<JSONObject> filteredDS = jsonObjDS.filter(
                jsonObj -> jsonObj.containsKey("tpsLast10Secs")||
                        (jsonObj.getString("serverName") != null && jsonObj.getString("world") != null)
        );

        // TODO 3.将日志信息，按种类分流，Item类，Entity类，TileEntity类，系统信息类
        //定义item侧输出流标签
        OutputTag<String> itemTag = new OutputTag<String>("item"){};
        //定义tileEntity侧输出流标签
        OutputTag<String> tileEntityTag = new OutputTag<String>("tileEntity"){};
        //定义Entity侧输出流标签
        OutputTag<String> entityTag = new OutputTag<String>("entity"){};


        SingleOutputStreamOperator<String> outputDS = filteredDS.process(
                new ProcessFunction<JSONObject, String>() {
                    @Override
                    public void processElement(JSONObject jsonObj, Context ctx, Collector<String> out) throws Exception {
                        // entityName itemName blockName为标志进行分流
                        boolean isItem = jsonObj.containsKey("itemName");
                        boolean isTileEntity = jsonObj.containsKey("blockName");
                        boolean isEntity = jsonObj.containsKey("entityName");
                        //将json格式转换为字符串，方便向侧输出流输出以及向消息队列中写入
                        String dataStr = jsonObj.toString();

                        if(isItem) {
                            //判断是否为物品日志
                            ctx.output(itemTag, dataStr);
                        }else if(isTileEntity) {
                            // 判断是否为方块日志
                            ctx.output(tileEntityTag, dataStr);
                        }else if(isEntity){
                            // 判断是否为实体日志日志
                            ctx.output(entityTag, dataStr);
                        }
                        else{
                            // 否则为系统日志
                            out.collect(dataStr);
                        }
                    }
                }
        );

        //获取侧输出流
        DataStream<String> itemDS = outputDS.getSideOutput(itemTag);
        DataStream<String> tileEntityDS = outputDS.getSideOutput(tileEntityTag);
        DataStream<String> entityDS = outputDS.getSideOutput(entityTag);

        //输出至Pravega中
        FlinkPravegaWriter<String> itemSink = MyPravegaUtil.getPravegaWriter(args, SCOPE_ITEM, STREAM_ITEM);
        itemDS.addSink(itemSink);

        FlinkPravegaWriter<String> tileEntitySink = MyPravegaUtil.getPravegaWriter(args, SCOPE_TILEENTITY, STREAM_TILEENTITY);
        tileEntityDS.addSink(tileEntitySink);

        FlinkPravegaWriter<String> entitySink = MyPravegaUtil.getPravegaWriter(args, SCOPE_ENTITY, STREAM_ENTITY);
        entityDS.addSink(entitySink);

        FlinkPravegaWriter<String> stateSink = MyPravegaUtil.getPravegaWriter(args, SCOPE_STATE, STREAM_STATE);
        outputDS.addSink(stateSink);

//        itemDS.print("page>>>>");
//        tileEntityDS.print("start>>>>");
//        entityDS.print("display>>>>");
//        outputDS.print();

        env.execute("Shunting Module");

    }
}
