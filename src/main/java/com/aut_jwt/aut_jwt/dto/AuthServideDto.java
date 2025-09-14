package com.aut_jwt.aut_jwt.dto;

import com.aut_jwt.aut_jwt.config.validator.BaseAuthValid;

public interface AuthServideDto {
    void AuthUser();
    BaseAuthValid register(BaseAuthValid user);
}
