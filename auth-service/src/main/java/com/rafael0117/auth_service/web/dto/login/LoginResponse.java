package com.rafael0117.auth_service.web.dto.login;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private String username;
    private List<String> roles;
    private long expirateAt;
}