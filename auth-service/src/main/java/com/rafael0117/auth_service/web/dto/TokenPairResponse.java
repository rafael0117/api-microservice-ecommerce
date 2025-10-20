package com.rafael0117.auth_service.web.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TokenPairResponse {
    private String username;
    private List<String> roles;
    private String accessToken;
    private long accessExpAt;
    private String refreshToken;
    private long refreshExpAt;
}