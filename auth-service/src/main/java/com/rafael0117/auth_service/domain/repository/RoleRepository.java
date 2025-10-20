package com.rafael0117.auth_service.domain.repository;

import com.rafael0117.auth_service.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,String> {

}
