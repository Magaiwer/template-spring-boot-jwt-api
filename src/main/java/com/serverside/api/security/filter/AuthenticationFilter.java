package com.serverside.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverside.api.property.JwtConfiguration;
import com.serverside.api.security.jwt.TokenProvider;
import com.serverside.api.service.LoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final JwtConfiguration jwtConfiguration;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting authentication. . .");
        LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);

        if (loginRequest == null)
            throw new UsernameNotFoundException("Unable to retrieve the username or password");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword(), Collections.emptyList());
        usernamePasswordAuthenticationToken.setDetails(loginRequest);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("Authentication was successful for the user '{}' ", authResult.getName());
        String token = jwtConfiguration.getType().equals("signed") ? tokenProvider.generateSignedToken(authResult) : tokenProvider.generateEncryptedToken(authResult);
        response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN, " + jwtConfiguration.getHeaderName());
        response.addHeader( jwtConfiguration.getHeaderName(), jwtConfiguration.getHeaderValue() + token);
    }
}
