package com.rafael0117.carrito_service.kafka.consumers;

import com.rafael0117.carrito_service.kafka.events.ProductChangedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventsConsumer {

    /**
     * Listener robusto:
     * - Si no defines app.kafka.topics.product-changed, usa "product-events"
     * - Si no defines spring.kafka.consumer.group-id, usa "carrito-service"
     * - Deserializa con el containerFactory que configuraste para ProductChangedPayload
     */
    @KafkaListener(
            topics   = "#{'${app.kafka.topics.productChanged:product-events}'}",
            groupId  = "#{'${spring.kafka.consumer.group-id:carrito-service}'}",
            containerFactory = "productChangedKafkaListenerContainerFactory"
            // Si quieres usar el factory por defecto de Spring Kafka, quita la línea de containerFactory.
    )
    public void onProductChanged(
            @Payload ProductChangedPayload evt,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            ConsumerRecord<String, ProductChangedPayload> record // útil para debug si hay issues
    ) {
        if (evt == null) {
            log.warn("[KAFKA] Mensaje nulo en topic={} partition={} offset={}", topic, partition, offset);
            return;
        }

        log.info("[KAFKA][carrito] topic={} key={} partition={} offset={} changeType={} id={} nombre={} precio={} stock={}",
                topic, key, partition, offset, evt.getChangeType(), evt.getId(), evt.getNombre(), evt.getPrecio(), evt.getStock());

        try {
            final String change = String.valueOf(evt.getChangeType()).toUpperCase();
            switch (change) {
                case "UPDATED" -> handleUpdated(evt);
                case "DELETED" -> handleDeleted(evt);
                case "CREATED" -> handleCreated(evt);
                default -> log.warn("[KAFKA][carrito] changeType no reconocido: {}", evt.getChangeType());
            }
        } catch (Exception e) {
            log.error("[KAFKA][carrito] Error procesando evento id={} : {}", evt.getId(), e.getMessage(), e);
            // A futuro: DLQ / retries
        }
    }

    private void handleUpdated(ProductChangedPayload evt) {
        // TODO: sincronizar precio/stock en los carritos
        log.info("[KAFKA][carrito] Sincronizando UPDATE de producto {}…", evt.getId());
    }

    private void handleDeleted(ProductChangedPayload evt) {
        // TODO: remover ítems o marcarlos inválidos
        log.info("[KAFKA][carrito] Eliminando producto {} de carritos (DELETED)…", evt.getId());
    }

    private void handleCreated(ProductChangedPayload evt) {
        // Normalmente solo log en carrito
        log.info("[KAFKA][carrito] Producto {} creado (CREATED).", evt.getId());
    }
}
