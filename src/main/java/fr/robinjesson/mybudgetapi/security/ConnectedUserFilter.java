package fr.robinjesson.mybudgetapi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ConnectedUserFilter extends OncePerRequestFilter {
    private final UserContextLoader userContextLoader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Si la requête est authentifiée
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            userContextLoader.loadUserFromSecurityContext();
        }

        filterChain.doFilter(request, response);
    }
}
