package com.serverside.api.security.filter;

import com.serverside.api.property.JwtConfiguration;
import com.serverside.api.security.jwt.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Magaiver Santos
 */
@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfiguration.getHeaderName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeaderValue())) {
            log.error("Token header invalid");
            chain.doFilter(request, response);
            return;
        }

        String decryptedToken = tokenProvider.decryptToken(header.replace(jwtConfiguration.getHeaderValue(), "").trim());

        tokenProvider.validateTokenSignature(decryptedToken);

        Authentication authentication = tokenProvider.getAuthentication(decryptedToken);

        if (tokenProvider.validateToken(decryptedToken, authentication)) {
            log.error("Invalid token");
            return;
        }

        log.info("Token is Valid ");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }


}
