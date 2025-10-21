package com.rafael0117.auth_service.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @Column(length = 40)
    private String name; // e.g., ROLE_ADMIN, ROLE_USER, ROLE_SUPER
}