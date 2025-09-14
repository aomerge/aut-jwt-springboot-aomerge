package com.aut_jwt.aut_jwt.config.security;

import com.aut_jwt.aut_jwt.dto.jwt.UserPrincipal;
import org.springframework.stereotype.Component;
//import java.security.Principal;

@Component("abacPolicyEvaluator")
public class AbacPolicyEvaluator {
    public boolean isOwner(String documentId, UserPrincipal principal) {
        return documentId.equals(principal.username());
    }

    public boolean canDelete(String documentId, UserPrincipal principal) {
        Boolean canDelete = (Boolean) principal.attrs().get("canDelete");
        return isOwner(documentId, principal) || Boolean.TRUE.equals(canDelete);
    }

    public boolean isTrusted(UserPrincipal principal) {
        return Boolean.TRUE.equals(principal.attrs().get("trusted"));
    }

    public boolean hasAccessToReports(UserPrincipal principal) {
        String department = (String) principal.attrs().get("department");
        Integer level = (Integer) principal.attrs().get("level");

        return "ADMIN".equals(department) && level != null && level >= 5;
    }
}
