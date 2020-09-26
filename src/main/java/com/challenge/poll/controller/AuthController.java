package com.challenge.poll.controller;

import com.challenge.poll.model.jpa.User;
import com.challenge.poll.payload.JwtAuthenticationResponse;
import com.challenge.poll.payload.LoginRequest;
import com.challenge.poll.repository.UserRepository;
import com.challenge.poll.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(value = "/token/generate")
    public ResponseEntity<?> register(@RequestBody User loginUser) throws AuthenticationException {
        System.out.println("We're in man!");
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                )
        );
        System.out.println("(Username, Password): (" + loginUser.getUsername() + ", " + loginUser.getPassword() + ")");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final Optional<User> user = userRepository.findByUsername(loginUser.getUsername());
        final String token = tokenProvider.generateToken(authentication);
        System.out.println("Token Controller Access=> Token Generated: " + token);
        return ResponseEntity.ok(token);
    }
}
