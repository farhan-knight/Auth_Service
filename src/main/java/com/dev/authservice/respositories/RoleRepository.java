package com.dev.authservice.respositories;

import com.dev.authservice.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository <Role, Long> {


    List<Role> findAllByIdIn(java.util.List<java.lang.Long> roleIds);
}
