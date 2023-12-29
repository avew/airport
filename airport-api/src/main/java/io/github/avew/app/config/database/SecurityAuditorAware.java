package io.github.avew.app.config.database;

/**
 * Created by avew on 1/15/18.
 */


import io.github.avew.app.config.security.SecurityUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/*
 * Developed by Asep Rojali on 12/18/18 7:25 PM
 * Last modified 12/18/18 5:58 PM
 * Copyright (c) 2018. All rights reserved.
 */

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SecurityAuditorAware implements AuditorAware<String> {


    @Override
    public Optional<String> getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserLogin();
        String s = userName != null ? userName : "system";
        return Optional.of(s);
    }
}
