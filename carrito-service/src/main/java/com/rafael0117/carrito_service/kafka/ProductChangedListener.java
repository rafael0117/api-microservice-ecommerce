package com.rafael0117.carrito_service.kafka;

import com.rafael0117.carrito_service.kafka.events.EventEnvelope;
import com.rafael0117.carrito_service.kafka.events.ProductChangedPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.springframework.kafka.support.KafkaHeaders.*;

@Slf4j
@Component
public class ProductChangedListener {
/*
    @KafkaListener(topics = "${app.topics.productChanged}", groupId = "carrito-service")
    public void onMessage(
            EventEnvelope<ProductChangedPayload> event,
            @Header(RECEIVED_TOPIC) String topic,
            @Header(RECEIVED_PARTITION) int partition,
            @Header(OFFSET) long offset,
            @Header(RECEIVED_KEY) String key
    ) {
        var payload = event.getPayload();

        log.info("Carrito <- Kafka: topic={} part={} offset={} key={} type={} change={} id={}",
                topic, partition, offset, key, event.getType(), payload.getChangeType(), payload.getId());

        // 🚧 Aquí va TU lógica de negocio:
        // - Si CREATED/UPDATED: actualizar/crear cache/catálogo local
        // - Si DELETED: marcar como no disponible
        // - Opcional: precalcular info para el carrito, etc.
    }

    */

}
