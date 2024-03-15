package com.example.finalproject.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

public class RoleIdentifier {
    public static boolean isPro(Principal principal) {
        Authentication authentication = (Authentication) principal;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream().anyMatch(authority -> authority.getAuthority().contains("PRO"));
    }
}
