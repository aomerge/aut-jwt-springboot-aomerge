package com.aut_jwt.aut_jwt.dto.jwt;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;


public record UserPrincipal(String username, Map<String, Object> attrs) {
}
