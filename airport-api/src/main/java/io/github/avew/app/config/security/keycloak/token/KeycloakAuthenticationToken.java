package io.github.avew.app.config.security.keycloak.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class KeycloakAuthenticationToken extends AbstractAuthenticationToken {

    private final KeycloakAccessToken accessToken;

    public KeycloakAuthenticationToken(KeycloakAccessToken accessToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.accessToken = accessToken;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public KeycloakAccessToken getPrincipal() {
        return accessToken;
    }

    public boolean hasRole(String role) {
        return getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_" + role));
    }

}
