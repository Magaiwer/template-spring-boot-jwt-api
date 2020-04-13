package com.serverside.api.resouces;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;
import java.util.List;

@RestController
@RequestMapping(value = "/login")
public class LoginResource {

    @PostMapping
    public String login(@AuthenticationPrincipal User user) {
        System.out.println("USER PERMISSIONS ");
        System.out.println(user.getAuthorities());
        return String.format("Authorities, %s!", user.getAuthorities());
    }
}
