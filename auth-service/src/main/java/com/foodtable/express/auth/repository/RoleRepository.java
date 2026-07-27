package com.foodtable.express.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodtable.express.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
