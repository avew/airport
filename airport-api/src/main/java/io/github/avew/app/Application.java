package io.github.avew.app;

import io.github.avew.app.config.properties.ApplicationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class, KeycloakSpringBootProperties.class})
@RequiredArgsConstructor
@Slf4j
public class Application {

    private final Environment env;
    public static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";
    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(Application.class);
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put(SPRING_PROFILE_DEFAULT, SPRING_PROFILE_DEVELOPMENT);
        app.setDefaultProperties(defProperties);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "APPLICATION '{}'\n\tIS RUNNING! ACCESS URLs:\n\t" +
                        "LOCAL: \t\t{}://localhost:{}\n\t" +
                        "EXTERNAL: \t{}://{}:{}\n\t" +
                        "VERSION: \t{}\n\t" +
                        "BUILD TIME: {}\n\t" +
                        "LOG LEVEL: {}\n\t" +
                        "PROFILE(S): {}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("app.application.version"),
                env.getProperty("app.application.timestamp"),
                env.getProperty("app.log.level"),
                env.getActiveProfiles());

    }

    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(SPRING_PROFILE_PRODUCTION)) {
            log.error("You have miss configured your application! It should not run " +
                    "with both the 'dev' and 'prod' profiles at the same time.");
        }
    }

}
