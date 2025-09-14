package com.aut_jwt.aut_jwt.controllers;

import com.aut_jwt.aut_jwt.dto.jwt.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class AdminController {

    @GetMapping("/reports")
    @PreAuthorize("@abacPolicyEvaluator.hasAccessToReports(Autorization)")
    public String adminReports(UserPrincipal principal) {
        return "Confidential admin reports";
    }
}
