package com.mcla.kdcwebspringboot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topic1() {
        return new NewTopic("ntpc-01", 3, (short) 1);
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic("ntpc-02", 5, (short) 1);
    }

}
