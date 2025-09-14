package com.aut_jwt.aut_jwt.config.validator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import com.aut_jwt.aut_jwt.config.validator.groups.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BaseAuthValid {
    @NotNull(groups = {OnAuth.class, OnCreate.class}, message = "El username o correo es requerido")
    @Schema(example = "alice")
    private String username;

    @NotNull(groups = {OnAuth.class, OnCreate.class}, message = "La contraseña es requerida")
    @Schema(example = "P@ssw0rd!")
    private String password;

    // NotNull(groups = {OnCreate.class}, message = "El rol es requerido")
    //private String role;
}
