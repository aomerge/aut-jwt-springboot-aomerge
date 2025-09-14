package com.aut_jwt.aut_jwt.config.validator;

import com.aut_jwt.aut_jwt.config.exeptions.ApiException;
import com.aut_jwt.aut_jwt.config.exeptions.TokenExpiredException;
import com.aut_jwt.aut_jwt.dto.ApiResponseDto;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final Tracer tracer;

    public GlobalExceptionHandler(@Autowired(required = false) Tracer tracer) {
        this.tracer = tracer;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return buildResponse(
                "Error de validación",
                errors,
                HttpStatus.BAD_REQUEST,
                request.getRequestURI()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        return buildResponse("Credenciales inválidas", "Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildResponse( ex.getMessage() , ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleGeneralException(Exception ex, HttpServletRequest request) {
        // Log the exception (optional)
        ex.printStackTrace();
        return buildResponse("Error interno del servidor", "Ocurrió un error inesperado", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponseDto> handleApiException(ApiException ex, HttpServletRequest request) {
        return buildResponse("Error de API", ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponseDto> handleTokenExpired(ApiException ex, HttpServletRequest request) {
        return buildResponse("Token Expirado", ex.getMessage(), ex.getStatus(), request.getRequestURI());
    }

    private ResponseEntity<ApiResponseDto> buildResponse(String error, Object message, HttpStatus status, String path) {
        String traceId = null;
        if (tracer != null && tracer.currentSpan() != null) {
            traceId = tracer.currentSpan().context().traceId();
            System.out.println(tracer);
        }
        log.error("Error procesando la petición [{}] - status: {}, error: {}, message: {}, traceId={}",
                path, status.value(), error, message, traceId);


        ApiResponseDto responseDto = new ApiResponseDto(
                error,
                status.value(),
                message,
                Instant.now(),
                path,
                traceId
        );
        return ResponseEntity.status(status).body(responseDto);
    }

}
