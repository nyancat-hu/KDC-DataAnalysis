import com.mcla.realtime.common.PravegaConstant;
import com.mcla.realtime.utils.PravegaUtils;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.stream.*;
import io.pravega.client.stream.impl.UTF8StringSerializer;
import io.pravega.connectors.flink.PravegaConfig;
import org.apache.flink.api.java.utils.ParameterTool;

import java.net.URI;

public class Test {
    public static void main(String[] args) {
        try (ReaderGroupManager readerGroupManager = ReaderGroupManager.withScope("mc-analysis", URI.create("tcp://192.168.88.244:9090"))) {
            ReaderGroupConfig readerGroupConfig = ReaderGroupConfig.builder()
                    .stream(Stream.of("mc-analysis", "mc-resources"))
                    .disableAutomaticCheckpoints()
                    .build();
            readerGroupManager.createReaderGroup("readerGroup", readerGroupConfig);
        }
        try (EventStreamClientFactory clientFactory = EventStreamClientFactory.withScope("mc-analysis",
                ClientConfig.builder().controllerURI(URI.create("tcp://192.168.88.244:9090")).build());
             EventStreamReader<String> reader = clientFactory.createReader("reader",
                     "readerGroup",
                     new UTF8StringSerializer(),
                     ReaderConfig.builder().build())) {
            String event = reader.readNextEvent(5000).getEvent();
            System.out.println(event);
        }


    }
}

