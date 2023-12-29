package io.github.avew.app.config.security.keycloak.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KeycloakAccess {

    private final List<String> roles;

    public KeycloakAccess() {
        this.roles = new ArrayList<>();
    }

    public KeycloakAccess(List<String> roles) {
        this.roles = Collections.unmodifiableList(roles);
    }

    public KeycloakAccess(String[] roles) {
        this(List.of(roles));
    }

    public List<String> getRoles() {
        return Collections.unmodifiableList(roles);
    }
}
