package com.aut_jwt.aut_jwt.services;

import com.aut_jwt.aut_jwt.config.validator.BaseAuthValid;
import com.aut_jwt.aut_jwt.dto.AuthServideDto;
import com.aut_jwt.aut_jwt.entity.Users;
import com.aut_jwt.aut_jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements AuthServideDto {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void AuthUser() {
    }

    @Override
    public BaseAuthValid register(BaseAuthValid user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        Users newUser = new Users();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encodedPassword);
        newUser.setRole("USER"); // Asignar rol por defecto
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) throw new IllegalArgumentException("Username already exists");
        userRepository.save(newUser);

        return user;
    }
}
