package com.example.demo.seguridadjwt.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // responde GET /api/hello
    @GetMapping("/api/hello")
    public String hello(@AuthenticationPrincipal UserDetails user) {
        if (user == null) return "No autenticado";
        return "Hola " + user.getUsername();
    }
}