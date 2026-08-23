package com.dev.authservice.services;

import com.dev.authservice.dtos.LoginResponseDto;
import com.dev.authservice.dtos.UserDto;
import com.dev.authservice.models.Role;
import com.dev.authservice.models.Session;
import com.dev.authservice.models.SessionStatus;
import com.dev.authservice.models.User;
import com.dev.authservice.respositories.RoleRepository;
import com.dev.authservice.respositories.SessionRepository;
import com.dev.authservice.respositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final String ISSUER = "http://localhost:8080";

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;
    private final JwtEncoder jwtEncoder;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository,
                       SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder,
                       JwtEncoder jwtEncoder,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
        this.jwtEncoder = jwtEncoder;

        this.roleRepository = roleRepository;
    }

    public ResponseEntity<String> login(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = optionalUser.get();
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Set<String> roles = user.getRoles().stream()
                .map(Role::getRole)
                .collect(Collectors.toSet());

        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.HOURS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .claim("userId", user.getId())
                .build();

        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        Session session = new Session();
        session.setStatus(SessionStatus.ACTIVE);
        session.setToken(jwt.getTokenValue());
        session.setUser(user);
        session.setExpiringAt(Date.from(expiresAt));
        sessionRepository.save(session);

        return ResponseEntity.ok(jwt.getTokenValue());
    }

    public ResponseEntity<Void> logout(String token) {
        Optional<Session> optionalSession = sessionRepository.findByToken(token);

        if (optionalSession.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Session session = optionalSession.get();
        session.setStatus(SessionStatus.INACTIVE);
        sessionRepository.save(session);

        return ResponseEntity.ok().build();
    }

    public UserDto signup(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        Role userRole = roleRepository.findByRole("USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setRole("USER");
                    return newRole; // no manual save — User.roles cascade persists it
                });

        user.setRoles(new java.util.HashSet<>(java.util.Set.of(userRole)));

        User savedUser = userRepository.save(user);
        return UserDto.from(savedUser);
    }

    public SessionStatus Validate(String token) {
        Optional<Session> optionalSession = sessionRepository.findByToken(token);
        if (optionalSession.isEmpty()) {
            return null;
        }
        Session session = optionalSession.get();
        if (!session.getStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.INACTIVE;
        }
        if (session.getExpiringAt().before(new Date())) {
            return SessionStatus.INACTIVE;
        }
        return SessionStatus.ACTIVE;
    }
}