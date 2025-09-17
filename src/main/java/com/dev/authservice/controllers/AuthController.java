package com.dev.authservice.controllers;


import com.dev.authservice.dtos.*;
import com.dev.authservice.models.SessionStatus;
import com.dev.authservice.services.AuthService;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

        private AuthService authService;

        public  AuthController(AuthService authService) {
            this.authService = authService;
        }

        @PostMapping("/login")
        public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto request) {
                return authService.login(request.getEmail(), request.getPassword());
        }

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
                return authService.logout(request.getToken(),request.getUserId());
        }

        @PostMapping("/signup")
        public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto request) {
                UserDto userDto = authService.signup(request.getEmail(), request.getPassword());
                return new ResponseEntity<>(userDto, HttpStatus.OK);
        }

        @PostMapping("/validate")
        public ResponseEntity<SessionStatus> Validate(@RequestBody ValidateTokenDto request ) {
                SessionStatus sessionStatus = authService.Validate(request.getToken(),request.getUserId());

                return new ResponseEntity<>(sessionStatus, HttpStatus.OK);
         }




}
