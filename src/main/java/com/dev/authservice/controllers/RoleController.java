package com.dev.authservice.controllers;

import com.dev.authservice.dtos.CreateRoleRequestDto;
import com.dev.authservice.models.Role;
import com.dev.authservice.services.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Role> createRole(CreateRoleRequestDto request) {
        Role role = roleService.createRole(request.getRole());
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}