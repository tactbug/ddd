package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.product.domain.category.Category;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaAdmin.NewTopics topics(){
        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(Category.class.getName())
                        .partitions(2)
                        .replicas(1)
                        .build()
        );
    }
}
