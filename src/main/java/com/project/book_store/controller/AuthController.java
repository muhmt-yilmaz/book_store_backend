package com.project.book_store.controller;

import com.project.book_store.entity.enums.RoleType;
import com.project.book_store.payload.request.CustomerRequest;
import com.project.book_store.payload.request.LoginRequest;
import com.project.book_store.payload.request.VerifyRequest;
import com.project.book_store.payload.response.AuthResponse;
import com.project.book_store.payload.response.CustomerResponse;
import com.project.book_store.security.jwt.JwtUtils;
import com.project.book_store.security.service.AuthService;
import com.project.book_store.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody @Valid LoginRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> registerUser(@RequestBody @Valid CustomerRequest customerRequest) {
        CustomerResponse response = authService.registerUser(customerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUserEmail(@RequestBody @Valid VerifyRequest verifyRequest) {
        String responseMessage = authService.verifyEmail(verifyRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ResponseEntity.ok("Yeni doğrulama kodu e-posta adresinize gönderildi.");
    }
}