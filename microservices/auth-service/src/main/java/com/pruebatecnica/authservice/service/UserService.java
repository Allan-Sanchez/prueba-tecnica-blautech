package com.pruebatecnica.authservice.service;

import com.pruebatecnica.authservice.util.JwtUtil;
import com.pruebatecnica.authservice.dto.*;
import com.pruebatecnica.authservice.entity.RefreshToken;
import com.pruebatecnica.authservice.entity.User;
import com.pruebatecnica.authservice.repository.RefreshTokenRepository;
import com.pruebatecnica.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;
    
    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
    
    public UserDto registerUser(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .shippingAddress(request.getShippingAddress())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());
        
        return convertToDto(savedUser);
    }
    
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        User user = userRepository.findByEmailForAuthentication(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password attempt for email: {}", request.getEmail());
            throw new BadCredentialsException("Credenciales inválidas");
        }
        
        refreshTokenRepository.revokeAllUserTokens(user.getId());
        
        String accessToken = jwtUtil.generateAccessToken(user, user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user, user.getId());
        
        saveRefreshToken(user.getId(), refreshToken);
        
        log.info("Login successful for user: {}", user.getId());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .user(convertToDto(user))
                .build();
    }
    
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();
        log.info("Refresh token request received");
        
        try {
            if (!jwtUtil.validateTokenFormat(refreshTokenValue)) {
                throw new BadCredentialsException("Token de refresh inválido");
            }
            
            if (!jwtUtil.isRefreshToken(refreshTokenValue)) {
                throw new BadCredentialsException("Token de refresh inválido");
            }
            
            String jti = jwtUtil.extractJti(refreshTokenValue);
            Long userId = jwtUtil.extractUserId(refreshTokenValue);
            
            RefreshToken storedToken = refreshTokenRepository.findByJtiAndRevokedFalse(jti)
                    .orElseThrow(() -> new BadCredentialsException("Token de refresh expirado o revocado"));
            
            if (!storedToken.isValid()) {
                refreshTokenRepository.revokeByJti(jti);
                throw new BadCredentialsException("Token de refresh expirado o revocado");
            }
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));
            
            refreshTokenRepository.revokeByJti(jti);
            
            String newAccessToken = jwtUtil.generateAccessToken(user, user.getId());
            String newRefreshToken = jwtUtil.generateRefreshToken(user, user.getId());
            
            saveRefreshToken(user.getId(), newRefreshToken);
            
            log.info("Token refresh successful for user: {}", user.getId());
            
            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(accessTokenExpiration)
                    .user(convertToDto(user))
                    .build();
                    
        } catch (Exception e) {
            log.error("Refresh token error: {}", e.getMessage());
            throw new BadCredentialsException("Token de refresh inválido");
        }
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return convertToDto(user);
    }
    
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            user.setEmail(request.getEmail());
        }
        
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getShippingAddress() != null) {
            user.setShippingAddress(request.getShippingAddress());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());
        
        return convertToDto(updatedUser);
    }
    
    public void logout(Long userId) {
        log.info("Logging out user: {}", userId);
        refreshTokenRepository.revokeAllUserTokens(userId);
    }
    
    private void saveRefreshToken(Long userId, String refreshToken) {
        try {
            String jti = jwtUtil.extractJti(refreshToken);
            LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000);
            
            RefreshToken tokenEntity = RefreshToken.builder()
                    .userId(userId)
                    .tokenHash(hashToken(refreshToken))
                    .jti(jti)
                    .expiresAt(expiresAt)
                    .revoked(false)
                    .build();
            
            refreshTokenRepository.save(tokenEntity);
        } catch (Exception e) {
            log.error("Error saving refresh token: {}", e.getMessage());
            throw new RuntimeException("Error al guardar token de refresh");
        }
    }
    
    private String hashToken(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(token.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear hash del token");
        }
    }
    
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .shippingAddress(user.getShippingAddress())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}