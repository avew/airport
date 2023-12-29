package io.github.avew.app.config.security.keycloak;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class KeycloackClientConfiguration {

    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    @Bean
    public Keycloak keycloack() {
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .serverUrl(keycloakSpringBootProperties.getAuthServerUrl())
                .realm(keycloakSpringBootProperties.getRealm())
                .clientId(keycloakSpringBootProperties.getResource())
                .clientSecret((String) keycloakSpringBootProperties.getCredentials().get("secret"))
                .resteasyClient(new ResteasyClientBuilder()
                        .connectionPoolSize(100)
                        .connectTimeout(5, TimeUnit.MINUTES)
                        .build())
                .build();
    }
}
