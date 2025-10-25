package com.rafael0117.auth_service.web.dto;


import com.rafael0117.auth_service.domain.model.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO  {
    private Long id;
    private String username;
    private Set<Role> roles; // nombres de rol (p.ej. ["ADMIN","USER"])
}