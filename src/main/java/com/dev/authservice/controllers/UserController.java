package com.dev.authservice.controllers;

import com.dev.authservice.dtos.SetUserRolesRequestDto;
import com.dev.authservice.dtos.UserDto;
import com.dev.authservice.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDto> getUserDetails(@PathVariable("id") Long userId) {
        UserDto userDto = userService.getUserDetails(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/roles")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserDto> setUserRoles(
            @PathVariable("id") Long userId,
            @RequestBody SetUserRolesRequestDto request) {
        UserDto userDto = userService.setUserRoles(userId, request.getRoleIds());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}