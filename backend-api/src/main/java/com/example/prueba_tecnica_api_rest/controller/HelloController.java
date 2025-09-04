package com.example.prueba_tecnica_api_rest.controller;

import com.example.prueba_tecnica_api_rest.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<ApiResponse<String>> hello() {
        long startTime = System.currentTimeMillis();
        
        ApiResponse<String> response = ApiResponse.success(
            "¡Hola Mundo desde Spring Boot!", 
            "Mensaje de saludo obtenido exitosamente.", 
            "OK"
        );
        
        response.setDurationMs(System.currentTimeMillis() - startTime);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> status() {
        long startTime = System.currentTimeMillis();
        
        ApiResponse<String> response = ApiResponse.success(
            "API funcionando correctamente", 
            "Estado de la API verificado.", 
            "OK"
        );
        
        response.setDurationMs(System.currentTimeMillis() - startTime);
        
        return ResponseEntity.ok(response);
    }
}