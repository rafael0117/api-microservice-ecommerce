package com.rafael0117.auth_service.web.dto.login;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequest {
    private String username;
    private String password;
}