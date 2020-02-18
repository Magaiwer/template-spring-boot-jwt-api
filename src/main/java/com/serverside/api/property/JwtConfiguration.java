package com.serverside.api.property;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration

@ConfigurationProperties(prefix = "spring.jwt.config")
@Getter @Setter @ToString
public class JwtConfiguration {
    private String loginUrl = "/login/**";
    private int expirationTimeMS = 3600;
    private String privateKey = "secrethere";
    private String type = "encrypted";
    private String headerName = "Authorization";
    private String headerValue =  "Bearer ";

}
