package com.rafael0117.carrito_service.kafka.config;

import com.rafael0117.carrito_service.kafka.events.ProductChangedPayload;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, ProductChangedPayload> productChangedConsumerFactory() {
        JsonDeserializer<ProductChangedPayload> valueDeserializer =
                new JsonDeserializer<>(ProductChangedPayload.class);
        valueDeserializer.addTrustedPackages(
                "com.rafael0117.producto_service.kafka.events",
                "com.rafael0117.carrito_service.kafka.events"
        );
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer.getClass());
        // Importante cuando múltiples servicios consumen el mismo tópico:
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductChangedPayload>
    productChangedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductChangedPayload> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productChangedConsumerFactory());
        factory.setConcurrency(1); // seguro para empezar
        return factory;
    }
}
