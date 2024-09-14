package fr.robinjesson.azertyapi.security;

import fr.robinjesson.azertyapi.entities.UserEntity;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserFilter implements Filter {

    private final AzertyUser azertyUser;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();

        // Check if the request is in the whitelist
        if (isWhitelisted(requestURI)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        final UserEntity user = getDecathlonUserAuthenticationOrThrowIfNotExist(getContextOfThrowIfNotExist());
        azertyUser.setEmail(user.getEmail());
        azertyUser.setUuid(user.getUuid());

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private UserEntity getDecathlonUserAuthenticationOrThrowIfNotExist(SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication.getPrincipal() instanceof UserEntity) {
            return (UserEntity)authentication.getPrincipal();
        } else {
            throw new SecurityException("User not found");
        }
    }

    private SecurityContext getContextOfThrowIfNotExist() {
        return (SecurityContext) Optional.ofNullable(SecurityContextHolder.getContext()).orElseThrow();
    }



    private boolean isWhitelisted(String requestURI) {
        for (String pattern : SecurityConfiguration.getWhitelist()) {
            if (pattern.endsWith("/**")) {
                String basePath = pattern.substring(0, pattern.length() - 3); // Remove "/**"
                if (requestURI.startsWith(basePath)) {
                    return true;
                }
            }
        }
        return false;
    }
}
