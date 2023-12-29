package io.github.avew.app.web.rest;

import io.github.avew.app.config.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/account")
public class AccountResource {

    @GetMapping(value = "/me")
    public String me() {
        return SecurityUtils.getCurrentUserLogin();
    }

}
