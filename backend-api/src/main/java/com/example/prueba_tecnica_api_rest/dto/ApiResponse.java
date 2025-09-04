package com.example.prueba_tecnica_api_rest.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApiResponse<T> {
    private boolean success;
    private int httpStatus = 200;
    private String appCode;
    private String message;
    private T data;
    private List<ErrorDetail> errors;
    private Meta meta;

    public ApiResponse() {
        this.errors = new ArrayList<>();
        this.meta = new Meta();
    }
    public static <T> ApiResponse<T> success(T data, String message, String appCode) {
        return success(data, message, appCode, 200);
    }

    public static <T> ApiResponse<T> success(T data, String message, String appCode, int httpStatus ) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.httpStatus = httpStatus;
        response.appCode = appCode;
        response.message = message;
        response.data = data;
        response.meta.timestamp = Instant.now();
        response.meta.service = "prueba-tecnica-api";
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
        response.meta.service = "prueba-tecnica-api";
        response.meta.version = "1.0.0";
        return response;
    }

    public void setRequestId(String requestId) {
        this.meta.requestId = requestId;
    }

    public void setDurationMs(long durationMs) {
        this.meta.durationMs = durationMs;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public static class ErrorDetail {
        private String appCode;
        private String message;

        public ErrorDetail() {}

        public ErrorDetail(String appCode, String message) {
            this.appCode = appCode;
            this.message = message;
        }

        public String getAppCode() {
            return appCode;
        }

        public void setAppCode(String appCode) {
            this.appCode = appCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Meta {
        private String requestId;
        private Instant timestamp;
        private String service;
        private String version;
        private long durationMs;

        public Meta() {
            this.requestId = UUID.randomUUID().toString();
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public Instant getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(long durationMs) {
            this.durationMs = durationMs;
        }
    }
}