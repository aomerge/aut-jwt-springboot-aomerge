package com.aut_jwt.aut_jwt.config.exeptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenExpiredException  extends  RuntimeException{
    private final HttpStatus status = HttpStatus.UNAUTHORIZED;
    public TokenExpiredException() {
        super("El token ha expirado.");
    }

}
