package com.rafael0117.auth_service.application.mapper;

import com.rafael0117.auth_service.domain.model.User;
import com.rafael0117.auth_service.web.dto.UsuarioDTO;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public UsuarioDTO toDto(User user){
        return UsuarioDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build();
    }
}
