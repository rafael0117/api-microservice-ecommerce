package com.rafael0117.carrito_service.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEnvelope<T> {

    private String type;       // p.ej. PRODUCT_CHANGED
    private String version;    // p.ej. 1
    private Instant occurredOn;
    private T payload;
}
