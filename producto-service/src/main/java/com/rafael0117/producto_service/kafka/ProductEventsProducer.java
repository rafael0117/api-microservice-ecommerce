package com.rafael0117.producto_service.kafka;

import com.rafael0117.producto_service.kafka.events.ProductChangedPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventsProducer {



    private final KafkaTemplate<String, ProductChangedPayload> kafkaTemplate;

    @Value("${app.kafka.topics.product-changed:product-events}")
    private String topic;

    public void publishProductChanged(ProductChangedPayload payload) {
        try {
            kafkaTemplate.send(topic, String.valueOf(payload.getId()), payload)
                    .whenComplete((res, ex) -> {
                        if (ex == null) {
                            log.info("[KAFKA] Enviado a topic={} key={} partition={} offset={}",
                                    topic,
                                    payload.getId(),
                                    res.getRecordMetadata().partition(),
                                    res.getRecordMetadata().offset());
                        } else {
                            log.error("[KAFKA] Error enviando evento: {}", ex.getMessage(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("[KAFKA] Falló envío de evento: {}", e.getMessage(), e);
        }
    }
}
