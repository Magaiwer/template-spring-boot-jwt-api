package com.serverside.api.security.filter;

import com.serverside.api.property.JwtConfiguration;
import com.serverside.api.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorizationFilter extends OncePerRequestFilter {
    private final JwtConfiguration jwtConfiguration;
    private final TokenProvider tokenProvider;

    @Override
    @NonNull
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(jwtConfiguration.getHeaderName());

        if (header == null || !header.startsWith(jwtConfiguration.getHeaderValue())) {
            chain.doFilter(request, response);
            return;
        }

        String decryptedToken = tokenProvider.decryptToken(header.replace(jwtConfiguration.getHeaderValue(), "").trim());
        tokenProvider.validateTokenSignature(decryptedToken);
        Authentication authentication = tokenProvider.getAuthentication(decryptedToken);

        if (!tokenProvider.validateToken(decryptedToken, authentication)) {
            log.error("Token expired");
            chain.doFilter(request, response);
            return;
        }

        log.info("Token is Valid ");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }


}
