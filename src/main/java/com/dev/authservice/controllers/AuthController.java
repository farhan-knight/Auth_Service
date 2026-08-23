package com.dev.authservice.controllers;

import com.dev.authservice.dtos.*;
import com.dev.authservice.models.SessionStatus;
import com.dev.authservice.services.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/auth")
public class AuthController {

        private AuthService authService;

        public AuthController(AuthService authService) {
                this.authService = authService;
        }

        @PostMapping(value = "/login", produces = MediaType.TEXT_PLAIN_VALUE)
        public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
                return authService.login(request.getEmail(), request.getPassword());
        }

        @PostMapping("/signup")
        public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto request) {
                UserDto userDto = authService.signup(request.getEmail(), request.getPassword());
                return new ResponseEntity<>(userDto, HttpStatus.OK);
        }


        @PostMapping("/logout")
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
                return authService.logout(jwt.getTokenValue());
        }

        @PostMapping("/validate")
        @SecurityRequirement(name = "bearerAuth")
        public ResponseEntity<SessionStatus> validate(@AuthenticationPrincipal Jwt jwt) {
                SessionStatus sessionStatus = authService.Validate(jwt.getTokenValue());
                return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
        }
}