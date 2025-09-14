package com.aut_jwt.aut_jwt.config.swagger;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Doc {
    String summary() default "";
    String description() default "";
    String[] tags() default {};
    boolean secured() default true; // si quieres marcar qué operaciones requieren bearer
}
