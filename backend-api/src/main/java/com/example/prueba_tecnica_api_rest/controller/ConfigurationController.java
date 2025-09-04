package com.example.prueba_tecnica_api_rest.controller;

import com.example.prueba_tecnica_api_rest.dto.ApiResponse;
import com.example.prueba_tecnica_api_rest.dto.ConfigurationDto;
import com.example.prueba_tecnica_api_rest.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConfigurationDto>>> getAllConfigurations() {
        long startTime = System.currentTimeMillis();
        
        try {
            List<ConfigurationDto> configurations = configurationService.getAllConfigurations();
            ApiResponse<List<ConfigurationDto>> response = ApiResponse.success(
                    configurations,
                    "Configuraciones obtenidas",
                    "OK",
                    201
            );
            
            response.setDurationMs(System.currentTimeMillis() - startTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<ConfigurationDto>> errorResponse = ApiResponse.error(
                500, 
                "INTERNAL_ERROR", 
                "Error interno del servidor."
            );
            errorResponse.setDurationMs(System.currentTimeMillis() - startTime);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<ApiResponse<ConfigurationDto>> getConfigurationByKey(@PathVariable String key) {
        long startTime = System.currentTimeMillis();
        try {
            ConfigurationDto configuration = configurationService.getConfigurationByKey(key);

            if (configuration == null) {
                ApiResponse<ConfigurationDto> errorResponse = ApiResponse.error(
                    404, 
                    "CONFIG_NOT_FOUND", 
                    "Configuración no encontrada."
                );
                errorResponse.setDurationMs(System.currentTimeMillis() - startTime);
                
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            ApiResponse<ConfigurationDto> response = ApiResponse.success(
                configuration, 
                "Configuración obtenida.", 
                "OK"
            );
            
            response.setDurationMs(System.currentTimeMillis() - startTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<ConfigurationDto> errorResponse = ApiResponse.error(
                500, 
                "INTERNAL_ERROR", 
                "Error interno del servidor."
            );
            errorResponse.setDurationMs(System.currentTimeMillis() - startTime);
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}