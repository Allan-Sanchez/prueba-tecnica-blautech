package com.pruebatecnica.authservice.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String firstName;
    
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    private String lastName;
    
    private String shippingAddress;
    
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 255, message = "El email no puede exceder 255 caracteres")
    private String email;
    
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;
}