package com.rafael0117.pedido_service.application.client;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private String name; // "ROLE_USER", "ROLE_ADMIN"
}