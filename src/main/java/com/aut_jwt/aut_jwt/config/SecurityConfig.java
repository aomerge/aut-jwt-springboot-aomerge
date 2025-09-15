package com.aut_jwt.aut_jwt.config;

import com.aut_jwt.aut_jwt.config.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests( authz -> authz
                    .requestMatchers("admin/**").hasRole("ADMIN")
                    .requestMatchers("reports/**").hasRole("ADMIN")
                    .requestMatchers("groups/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("user/**").hasAnyRole("USER","OWNER" )
                    .requestMatchers(
                            "/auth/**",
                            "/v3/api-docs/**",
                            "/v3/api-docs.yaml",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/webjars/**"
                    ).permitAll()
                    .anyRequest().authenticated() // All other requests require authentication
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
