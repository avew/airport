package io.github.avew.app.config.security;

import io.github.avew.app.config.security.keycloak.token.KeycloakAccessToken;
import io.github.avew.app.config.security.keycloak.token.KeycloakAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SecurityUtils {

    public SecurityUtils() {
    }

    @SuppressWarnings("unchecked")
    private static KeycloakAccessToken getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof KeycloakAuthenticationToken kat)
            return kat.getPrincipal();

        return null;
    }

    public static String getCurrentUserLogin() {
        return Optional.ofNullable(getPrincipal())
                .map(KeycloakAccessToken::getUsername)
                .orElse(null);
    }

    public static String getCurrentUserId() {
        return Optional.ofNullable(getPrincipal())
                .map(KeycloakAccessToken::getSubject)
                .orElse(null);

    }

    public static String getTokenString() {
        return Optional.ofNullable(getPrincipal())
                .map(KeycloakAccessToken::getTokenValue)
                .orElse(null);
    }

    public static KeycloakAccessToken getToken() {
        return getPrincipal();
    }

    public static Set<String> getRoles() {
        KeycloakAccessToken authentication = getToken();
        return Optional.ofNullable(authentication)
                .map(v -> authentication
                        .getRealmAccess()
                        .getRoles())
                .map(HashSet::new)
                .orElse(new HashSet<>());
    }

    public static boolean isSuperAdmin() {
        return Optional.ofNullable(getPrincipal())
                .map(e -> e.hasAnyRole(RoleConstant.ROLE_SUPER_ADMIN))
                .orElse(false);
    }


}
