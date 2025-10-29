package com.rafael0117.auth_service.web.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegistrarRequestDTO {
    private String username;
    private String password;
    private String email;
}