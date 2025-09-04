package com.example.prueba_tecnica_api_rest.controller;

import com.example.prueba_tecnica_api_rest.dto.*;
import com.example.prueba_tecnica_api_rest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        
        try {
            AuthResponse authResponse = userService.login(request);
            
            ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("LOGIN_OK")
                    .message("Inicio de sesión exitoso")
                    .data(authResponse)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Invalid login attempt for email: {}", request.getEmail());
            
            ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                    .success(false)
                    .httpStatus(HttpStatus.UNAUTHORIZED.value())
                    .appCode("AUTH_INVALID_CREDENTIALS")
                    .message("Credenciales inválidas")
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");
        
        try {
            AuthResponse authResponse = userService.refreshToken(request);
            
            ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("LOGIN_OK")
                    .message("Token renovado exitosamente")
                    .data(authResponse)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            log.warn("Invalid refresh token attempt");
            
            ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                    .success(false)
                    .httpStatus(HttpStatus.UNAUTHORIZED.value())
                    .appCode("AUTH_TOKEN_EXPIRED")
                    .message("Token de refresh inválido o expirado")
                    .build();
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}