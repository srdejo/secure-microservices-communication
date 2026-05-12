package com.linktic.productservice.infrastructure.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${api.key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        if (requestPath.contains("/swagger-ui") || 
            requestPath.contains("/v3/api-docs") || 
            requestPath.contains("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestKey = request.getHeader("X-API-KEY");

        if (apiKey.equals(requestKey)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/vnd.api+json");
            
            String errorJson = "{\"errors\":[{\"status\":\"401\",\"code\":\"UNAUTHORIZED\",\"detail\":\"Invalid API Key\"}]}";
            response.getWriter().write(errorJson);
        }
    }
}
