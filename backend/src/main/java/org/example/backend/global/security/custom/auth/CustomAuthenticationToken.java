package org.example.backend.global.security.custom.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String type;

    public CustomAuthenticationToken(Object principal, Object credentials, String type) {
        super(principal, credentials);
        this.type = type;
    }

    // 권한까지 받는 새로운 생성자
    public CustomAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String type) {
        super(principal, credentials, authorities);
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
