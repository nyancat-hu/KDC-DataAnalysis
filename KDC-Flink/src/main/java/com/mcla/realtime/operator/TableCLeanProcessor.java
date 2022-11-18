package com.mcla.realtime.operator;

import com.mcla.realtime.utils.MySQLUtil;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;

import java.util.Iterator;

/**
 * @Description: 在写入数据前先对数据表进行清空
 * @ClassName: TableCLeanProcessor
 * @Author: ice_light
 * @Date: 2022/11/19 1:47
 * @Version: 1.0
 */
public class TableCLeanProcessor extends ProcessAllWindowFunction<String, String, GlobalWindow> {
    private final String tableName;

    public TableCLeanProcessor(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void process(Context context, Iterable<String> elements, Collector<String> out) throws Exception {
        MySQLUtil.truncateTable(tableName);
        for (String element : elements) out.collect(element);
    }
}
