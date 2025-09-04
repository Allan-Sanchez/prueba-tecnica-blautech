package com.example.prueba_tecnica_api_rest.service;

import com.example.prueba_tecnica_api_rest.dto.ConfigurationDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigurationService {

    public List<ConfigurationDto> getAllConfigurations() {
        // Mock data - in a real application this would come from database
        List<ConfigurationDto> configurations = new ArrayList<>();
        
        configurations.add(new ConfigurationDto(
            1L,
            "version_app",
            "1.0.0",
            Instant.parse("2025-08-24T08:35:31.809Z"),
            Instant.parse("2025-08-24T08:35:31.809Z")
        ));
        
        configurations.add(new ConfigurationDto(
            2L,
            "max_upload_size",
            "10MB",
            Instant.parse("2025-08-24T08:35:31.809Z"),
            Instant.parse("2025-08-24T08:35:31.809Z")
        ));
        
        configurations.add(new ConfigurationDto(
            3L,
            "api_timeout",
            "30000",
            Instant.parse("2025-08-24T08:35:31.809Z"),
            Instant.parse("2025-08-24T08:35:31.809Z")
        ));

        return configurations;
    }

    public ConfigurationDto getConfigurationByKey(String key) {
        return getAllConfigurations().stream()
            .filter(config -> config.getKey().equals(key))
            .findFirst()
            .orElse(null);
    }
}