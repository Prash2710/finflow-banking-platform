package com.finflow.auth.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @GetMapping
    @PreAuthorize("hasAuthority(RoleConstants.CUSTOMER)")
    public String customer() {
        return "Welcome Customer";
    }
}