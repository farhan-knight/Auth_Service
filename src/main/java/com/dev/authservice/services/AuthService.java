package com.dev.authservice.services;


import com.dev.authservice.dtos.UserDto;
import com.dev.authservice.models.Role;
import com.dev.authservice.models.Session;
import com.dev.authservice.respositories.SessionRepository;
import com.dev.authservice.models.SessionStatus;
import com.dev.authservice.models.User;
import com.dev.authservice.respositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@Primary
public class AuthService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;

    public AuthService(UserRepository userRepository,
                       SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder ) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.sessionRepository = sessionRepository;
    }

    public ResponseEntity<UserDto> login(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }

        //Generating JWT Token
        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm alg = Jwts.SIG.HS512; //or HS384 or HS256
        SecretKey key = alg.key().build();

        Map<String,Object> jsonMap = new HashMap<>();
        jsonMap.put("email", user.getEmail());
        jsonMap.put("roles", user.getRoles());
        jsonMap.put("createdAt",new Date());
        jsonMap.put("ExpiresAt", DateUtils.addDays(new Date(), 30));

      // Create the compact JWS:
        String jws = Jwts.builder()
                .claims(jsonMap)
                .signWith(key, alg)
                .compact();

        Session session = new Session();
        session.setStatus(SessionStatus.ACTIVE);
        session.setToken(jws);
        session.setUser(user);
        session.setExpiringAt((Date) jsonMap.get("ExpiresAt"));
        sessionRepository.save(session);

        UserDto userDto = new UserDto();

        userDto.setEmail(email);
        MultiValueMapAdapter<String, String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add(HttpHeaders.SET_COOKIE, "auth-token:" + jws);

        ResponseEntity<UserDto> response = new ResponseEntity<>(userDto, headers, HttpStatus.OK);

        return response;

    }

    public ResponseEntity<Void> logout(String token, Long userId) {
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, userId);

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
        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    public SessionStatus Validate(String token,Long userId) {
        Optional<Session> optionalSession = sessionRepository.findByTokenAndUser_Id(token, userId);

        if(optionalSession.isEmpty()) {
            return null;
        }

        Session session = optionalSession.get();

        if(!session.getStatus().equals(SessionStatus.ACTIVE)) {
            return SessionStatus.INACTIVE;
        }

        Date Currtime = new Date();

        if(session.getExpiringAt().before(Currtime)) {
            return SessionStatus.INACTIVE;
        }

        //JWT Decoding.
        Jws<Claims> jwsClaims = Jwts.parser().build().parseSignedClaims(token);

        // Map<String, Object> -> Payload object or JSON
        String email = (String) jwsClaims.getPayload().get("email");
        List<Role> roles = (List<Role>) jwsClaims.getPayload().get("roles");
        Date createdAt = (Date) jwsClaims.getPayload().get("createdAt");

//        if (restrictedEmails.contains(email)) {
//            //reject the token
//        }

        return SessionStatus.ACTIVE;


    }

}
