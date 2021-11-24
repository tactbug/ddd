package com.tactbug.ddd.product.outbound.publisher;

import com.tactbug.ddd.product.TactProductApplication;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Collections;
import java.util.HashMap;

@Configuration
public class KafkaProducerConfig {

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

    @Bean("bytesKafkaTemplate")
    public KafkaTemplate<String, byte[]> bytesTemplate(ProducerFactory<String, byte[]> pf) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new KafkaTemplate<>(pf, properties);
    }
}
