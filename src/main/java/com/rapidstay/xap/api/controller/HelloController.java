package com.rapidstay.xap.api.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/secure/hello")
    public String secureHello(Authentication authentication) {
        return "Hello, " + authentication.getName() + " 👋";
    }
}
