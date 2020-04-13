package com.serverside.api;

import com.serverside.api.property.JwtConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableConfigurationProperties(value = JwtConfiguration.class)
public class ApiApplication {

    public static void main(String[] args) {
        System.out.println(  "Private key ----->        " + new BCryptPasswordEncoder().encode("1234"));
        SpringApplication.run(ApiApplication.class, args);
    }

}
