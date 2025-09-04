package com.example.prueba_tecnica_api_rest.controller;

import com.example.prueba_tecnica_api_rest.dto.*;
import com.example.prueba_tecnica_api_rest.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration request for email: {}", request.getEmail());
        
        try {
            UserDto userDto = userService.registerUser(request);
            
            ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.CREATED.value())
                    .appCode("USER_REGISTERED")
                    .message("Usuario registrado exitosamente")
                    .data(userDto)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed for email {}: {}", request.getEmail(), e.getMessage());
            
            ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.CONFLICT.value())
                    .appCode("VALIDATION_ERROR")
                    .message(e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("Getting current user profile for email: {}", email);
            
            // Por ahora, vamos a simular que obtenemos el ID del usuario
            // En una implementación real, esto vendría del JWT o del contexto de seguridad
            Long userId = 1L; // Esto debería venir del JWT
            
            UserDto userDto = userService.getUserById(userId);
            
            ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("PROFILE_FETCHED")
                    .message("Perfil de usuario obtenido exitosamente")
                    .data(userDto)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("User profile fetch failed: {}", e.getMessage());
            
            ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.NOT_FOUND.value())
                    .appCode("USER_NOT_FOUND")
                    .message("Usuario no encontrado")
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("Updating current user profile for email: {}", email);
            
            // Por ahora, vamos a simular que obtenemos el ID del usuario
            // En una implementación real, esto vendría del JWT o del contexto de seguridad
            Long userId = 1L; // Esto debería venir del JWT
            
            UserDto userDto = userService.updateUser(userId, request);
            
            ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("PROFILE_UPDATED")
                    .message("Perfil de usuario actualizado exitosamente")
                    .data(userDto)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("User profile update failed: {}", e.getMessage());
            
            if (e.getMessage().contains("email ya está registrado")) {
                ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                        .success(false)
                        .httpStatus(HttpStatus.CONFLICT.value())
                        .appCode("VALIDATION_ERROR")
                        .message(e.getMessage())
                        .build();
                
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                    .success(false)
                    .httpStatus(HttpStatus.NOT_FOUND.value())
                    .appCode("USER_NOT_FOUND")
                    .message("Usuario no encontrado")
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            log.info("Logout request for email: {}", email);
            
            // Por ahora, vamos a simular que obtenemos el ID del usuario
            // En una implementación real, esto vendría del JWT o del contexto de seguridad
            Long userId = 1L; // Esto debería venir del JWT
            
            userService.logout(userId);
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(true)
                    .httpStatus(HttpStatus.OK.value())
                    .appCode("LOGOUT_SUCCESS")
                    .message("Sesión cerrada exitosamente")
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            
            ApiResponse<Void> response = ApiResponse.<Void>builder()
                    .success(false)
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .appCode("LOGOUT_ERROR")
                    .message("Error al cerrar sesión")
                    .build();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}