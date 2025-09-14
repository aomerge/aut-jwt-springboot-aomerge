package com.aut_jwt.aut_jwt;

import com.aut_jwt.aut_jwt.config.secrets.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class AutJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutJwtApplication.class, args);
	}

}
