package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.product.TactProductApplication;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin.NewTopics topics(){
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(TactProductApplication.APPLICATION_NAME + "-" + EventTopics.CATEGORY.getName())
                        .partitions(2)
                        .replicas(1)
                        .build(),
                TopicBuilder.name(TactProductApplication.APPLICATION_NAME + "-" + EventTopics.BRAND.getName())
                        .partitions(2)
                        .replicas(1)
                        .build()
        );
    }
}
