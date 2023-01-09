package com.mcla.realtime.utils;

import com.mcla.realtime.common.PravegaConstant;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.Stream;
import io.pravega.client.stream.StreamConfiguration;
import io.pravega.connectors.flink.FlinkPravegaReader;
import io.pravega.connectors.flink.FlinkPravegaWriter;
import io.pravega.connectors.flink.PravegaConfig;
import io.pravega.connectors.flink.PravegaEventRouter;
import io.pravega.connectors.flink.serialization.PravegaSerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class MyPravegaUtil {

    public static Stream createStream(PravegaConfig pravegaConfig, String streamName) {
        return createStream(pravegaConfig, streamName, StreamConfiguration.builder().build());
    }

    public static Stream createStream(PravegaConfig pravegaConfig, String streamName, StreamConfiguration streamConfig) {
        // resolve the qualified name of the stream
        Stream stream = pravegaConfig.resolve(streamName);

        try(StreamManager streamManager = StreamManager.create(pravegaConfig.getClientConfig())) {
            // create the requested scope (if necessary)
            streamManager.createScope(stream.getScope());

            // create the requested stream based on the given stream configuration
            streamManager.createStream(stream.getScope(), stream.getStreamName(), streamConfig);
        }

        return stream;
    }

    public static FlinkPravegaReader<String> getPravegaReader(String[] args, String scope, String stream) {
        ParameterTool params = ParameterTool.fromArgs(args);
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withDefaultScope(scope);
        Stream pravegastream = createStream(
                pravegaConfig,
                params.get(PravegaConstant.STREAM_PARAM, stream));
        return FlinkPravegaReader.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(pravegastream)
                .withDeserializationSchema(new SimpleStringSchema())
                .build();
    }

    //FlinkPravegaWriter封装
    public static FlinkPravegaWriter<String> getPravegaWriter(String[] args, String scope, String stream) {
        ParameterTool params = ParameterTool.fromArgs(args);
        PravegaConfig pravegaConfig = PravegaConfig
                .fromParams(params)
                .withDefaultScope(scope);
        Stream pravegastream = createStream(
                pravegaConfig,
                params.get(PravegaConstant.STREAM_PARAM, stream));
        return FlinkPravegaWriter.<String>builder()
                .withPravegaConfig(pravegaConfig)
                .forStream(stream)
                .withEventRouter(new EventRouter())
                .withSerializationSchema(new SimpleStringSchema())
                .build();
    }

    public static class EventRouter implements PravegaEventRouter<String> {
        // Ordering - events with the same routing key will always be
        // read in the order they were written
        @Override
        public String getRoutingKey(String event) {
            return "SameRoutingKey";
        }
    }
}
