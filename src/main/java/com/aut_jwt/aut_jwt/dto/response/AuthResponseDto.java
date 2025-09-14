package com.aut_jwt.aut_jwt.dto.response;

import lombok.Getter;

@Getter
public class AuthResponseDto {
    private String status = "success";
    private String type = "Bearer";
    private String username;
    private String token;

    public AuthResponseDto(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public AuthResponseDto(String token) {
    }
}

