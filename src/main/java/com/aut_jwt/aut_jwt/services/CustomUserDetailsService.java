package com.aut_jwt.aut_jwt.services;

import com.aut_jwt.aut_jwt.entity.Users;
import com.aut_jwt.aut_jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario en base de datos
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<SimpleGrantedAuthority> authorities = user.getRolesUsers().stream()
                .map(roleUser -> new SimpleGrantedAuthority("ROLE_" + roleUser.getRole().getName()))
                .toList();

        // Devolver un objeto UserDetails con los datos de seguridad
        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
