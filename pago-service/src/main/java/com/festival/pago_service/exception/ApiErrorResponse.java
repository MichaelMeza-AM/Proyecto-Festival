package com.festival.pago_service.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    
    private OffsetDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}