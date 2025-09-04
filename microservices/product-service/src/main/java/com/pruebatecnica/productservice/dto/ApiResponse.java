package com.pruebatecnica.productservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    @Builder.Default
    private boolean success = false;
    @Builder.Default
    private int httpStatus = 200;
    private String appCode;
    private String message;
    private T data;
    @Builder.Default
    private List<ErrorDetail> errors = new ArrayList<>();
    @Builder.Default
    private Meta meta = new Meta();
    
    public static <T> ApiResponse<T> success(T data, String message, String appCode) {
        return success(data, message, appCode, 200);
    }

    public static <T> ApiResponse<T> success(T data, String message, String appCode, int httpStatus) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.httpStatus = httpStatus;
        response.appCode = appCode;
        response.message = message;
        response.data = data;
        response.meta.timestamp = Instant.now();
        response.meta.service = "product-service";
        response.meta.version = "1.0.0";
        return response;
    }

    public static <T> ApiResponse<T> error(int httpStatus, String appCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.httpStatus = httpStatus;
        response.appCode = appCode;
        response.message = message;
        response.errors.add(new ErrorDetail(appCode, message));
        response.meta.timestamp = Instant.now();
        response.meta.service = "product-service";
        response.meta.version = "1.0.0";
        return response;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorDetail {
        private String appCode;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Meta {
        @Builder.Default
        private String requestId = UUID.randomUUID().toString();
        private Instant timestamp;
        private String service;
        private String version;
        private long durationMs;
    }
}