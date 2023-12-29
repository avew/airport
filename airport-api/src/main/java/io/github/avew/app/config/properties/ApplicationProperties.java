package io.github.avew.app.config.properties;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/*
 * Developed by Asep Rojali on 12/18/18 7:25 PM
 * Last modified 12/18/18 5:20 PM
 * Copyright (c) 2018. All rights reserved.
 */

@Getter
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final CorsConfiguration cors = new CorsConfiguration();
    private final ApplicationVersionProperties application = new ApplicationVersionProperties();

    public ApplicationProperties() {
    }

}
