package com.serverside.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serverside.api.domain.User;
import com.serverside.api.security.jwt.TokenProvider;
import com.serverside.api.service.AppUser;
import com.serverside.api.service.CustomUserDetailService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("Attempting authentication. . .");

        AppUser appUser = new ObjectMapper().readValue(request.getInputStream(), AppUser.class);
        //log.info(" username '{}'", appUser.getUsername());

        if (appUser == null)
            throw new UsernameNotFoundException("Unable to retrieve the username or password");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword(), Collections.emptyList());
        usernamePasswordAuthenticationToken.setDetails(appUser);

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        log.info("Authentication was successful for the user '{}' ", authResult.getName());

        String token =  tokenProvider.generateEncryptedToken(authResult);

        Cookie cookie = new Cookie("token", token);
        cookie.setSecure(true);
        response.addCookie(cookie);
        chain.doFilter(request, response);

    }





}
