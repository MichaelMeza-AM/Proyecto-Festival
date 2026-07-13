package com.festival.escenario_service.exception;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest(BadRequestException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    // NUEVO: Atrapa las validaciones automáticas de @Valid (DTOs)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidations(
            org.springframework.web.bind.MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        // Extraemos todos los mensajes de error de los campos y los unimos con una coma
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(java.util.stream.Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, "Error de validación: " + errorMessage, request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        String mensaje = "Método no permitido. " + ex.getMessage();
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, mensaje, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado: " + ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(error, status);
    }
}