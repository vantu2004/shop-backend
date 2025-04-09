package com.vantu.shop_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vantu.shop_backend.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String string);

}
