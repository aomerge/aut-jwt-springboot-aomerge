package com.aut_jwt.aut_jwt.controllers;

import com.aut_jwt.aut_jwt.dto.jwt.UserPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @GetMapping("/{id}")
    @PreAuthorize("@abacPolicyEvaluator.isOwner(#id, Autentiquetion)")
    public String getDocument(@PathVariable String id, UserPrincipal principal) {
        return "Document content for ID " + id;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@abacPolicyEvaluator.canDelete(#id, Autentiquetion)")
    public String deleteDocument(@PathVariable String id, UserPrincipal principal) {
        return "Deleted document with ID " + id;
    }

    @PostMapping
    @PreAuthorize("@abacPolicyEvaluator.isTrusted(Autentiquetion)")
    public String createDocument(UserPrincipal principal) {
        return "Document created by " + principal.username();
    }
}
