package io.github.avew.app.config.security.keycloak.token;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class KeycloakAccessToken implements OAuth2Token, Principal {

    private static final Gson GSON = new Gson();
    public static final String CLAIM_USERNAME = "preferred_username";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_GIVEN_NAME = "given_name";
    public static final String CLAIM_FAMILY_NAME = "family_name";
    public static final String CLAIM_REALM_ACCESS = "realm_access";
    public static final String CLAIM_RESOURCE_ACCESS = "resource_access";


    private final Jwt delegate;

    private final String subject;

    private final String username;

    private final String givenName;

    private final String lastName;

    private final String email;

    private final KeycloakAccess realmAccess;

    @Getter(AccessLevel.NONE)
    private final Map<String, KeycloakAccess> resourceAccess = new LinkedHashMap<>();

    public KeycloakAccessToken(Jwt jwt) {
        this.delegate = jwt;
        this.subject = jwt.getSubject();
        this.username = jwt.getClaimAsString(CLAIM_USERNAME);
        this.givenName = jwt.getClaimAsString(CLAIM_GIVEN_NAME);
        this.lastName = jwt.getClaimAsString(CLAIM_FAMILY_NAME);
        this.email = jwt.getClaimAsString(CLAIM_EMAIL);

        Map<String, Object> realmAccess = jwt.getClaimAsMap(CLAIM_REALM_ACCESS);
        if (realmAccess != null) {
            this.realmAccess = GSON.fromJson(
                    GSON.toJson(realmAccess),
                    KeycloakAccess.class
            );
        }
        else this.realmAccess = null;

        Map<String, Object> resourceAccess = jwt.getClaimAsMap(CLAIM_RESOURCE_ACCESS);
        if (resourceAccess != null && !resourceAccess.isEmpty()) {
            for (String resource : resourceAccess.keySet()) {
                Object o = resourceAccess.get(resource);
                this.resourceAccess.put(resource, GSON.fromJson(
                        GSON.toJson(o),
                        KeycloakAccess.class
                ));
            }
        }
    }

    @Override
    public String getTokenValue() {
        return delegate.getTokenValue();
    }

    @Override
    public Instant getIssuedAt() {
        return delegate.getIssuedAt();
    }

    @Override
    public String getName() {
        return getUsername();
    }

    @Override
    public Instant getExpiresAt() {
        return delegate.getExpiresAt();
    }

    public Map<String, KeycloakAccess> getResourceAccess() {
        return Collections.unmodifiableMap(resourceAccess);
    }

    public Map<String, Object> getOtherClaims() {
        return delegate.getClaims();
    }

    public String getClaimAsString(String key) {
        return delegate.getClaimAsString(key);
    }


    public boolean hasAnyRole(String role) {
        for (String realmRole : realmAccess.getRoles()) {
            if (realmRole.equalsIgnoreCase(role)) return true;
        }
        for (String resource : resourceAccess.keySet()) {
            KeycloakAccess ka = resourceAccess.get(resource);
            for (String resourceRole : ka.getRoles()) {
                if (resourceRole.equalsIgnoreCase(role)) return true;
            }
        }
        return false;
    }

}
