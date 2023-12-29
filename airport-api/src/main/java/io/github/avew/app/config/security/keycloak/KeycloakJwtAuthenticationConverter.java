package io.github.avew.app.config.security.keycloak;

import io.github.avew.app.config.security.keycloak.token.KeycloakAccessToken;
import io.github.avew.app.config.security.keycloak.token.KeycloakAuthenticationToken;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(source).stream(),
                mapRoles(source).stream()
        ).collect(Collectors.toSet());

        return new KeycloakAuthenticationToken(
                new KeycloakAccessToken(source),
                authorities
        );
    }

    public Collection<? extends GrantedAuthority> mapRoles(Jwt jwt) {
        Set<GrantedAuthority> results = new LinkedHashSet<>();

        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);
        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);

        String rolePrefix = "ROLE_";
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            for (String role : roles)
                results.add(new SimpleGrantedAuthority(rolePrefix + role));
        }

        if (resourceAccess != null) {
            for (String resource : resourceAccess.keySet()) {
                Map<String, Object> resourceMap = (Map<String, Object>) resourceAccess.get(resource);
                if (resourceMap.containsKey("roles")) {
                    for (String role : (Collection<String>) resourceMap.get("roles"))
                        results.add(new SimpleGrantedAuthority(String.format(
                                "%s%s_%s",
                                rolePrefix,
                                resource,
                                role
                        )));
                }
            }
        }

        return Collections.unmodifiableSet(results);
    }

}
