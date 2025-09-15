package com.aut_jwt.aut_jwt.controllers;

import com.aut_jwt.aut_jwt.config.security.JwtTokenProviderImp;
import com.aut_jwt.aut_jwt.config.swagger.Doc;
import com.aut_jwt.aut_jwt.config.validator.BaseAuthValid;
import com.aut_jwt.aut_jwt.config.validator.groups.OnAuth;
import com.aut_jwt.aut_jwt.config.validator.groups.OnCreate;
import com.aut_jwt.aut_jwt.dto.AuthServideDto;
import com.aut_jwt.aut_jwt.dto.response.AuthResponseDto;
import com.aut_jwt.aut_jwt.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@RestController
@RequestMapping("/auth")
@Doc(tags = {"Auth"}, description = "Autenticación y registro de usuarios")
public class AuthController {
    @Autowired
    private AuthServideDto authServide;

    @Autowired
    private JwtTokenProviderImp jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/test")
    @Doc(summary = "Prueba de salud del módulo de autenticación", secured = false)
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Authenticate exitosa");
    }


    @Doc(summary = "Inicio de sesión y emisión de JWT")
    @PostMapping("/login")
    public ResponseEntity<?>  login(@Validated(OnAuth.class) @RequestBody BaseAuthValid loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                )
        );
        // Si pasa, generar token
        String token = jwtTokenProvider.createToken(loginRequest.getUsername(), authentication);
        AuthResponseDto authResponse = new AuthResponseDto(token, loginRequest.getUsername());
        // Envolver respuesta
        return ResponseEntity.ok(authResponse);
    }

    @Doc(summary = "Registro de usuario")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated(OnCreate.class) @RequestBody BaseAuthValid user) {
        BaseAuthValid authResponse = authServide.register(user);
        // Crear respuesta sin exponer datos sensibles
        BaseAuthValid response = new BaseAuthValid();
        response.setUsername(authResponse.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
