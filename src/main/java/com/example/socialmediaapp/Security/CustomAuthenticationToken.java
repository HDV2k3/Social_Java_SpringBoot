package com.example.socialmediaapp.Security;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String ipAddress;
    private final String userAgent;

    public CustomAuthenticationToken(Object principal, Object credentials, String ipAddress, String userAgent) {
        super(principal, credentials);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public CustomAuthenticationToken(Object principal, Object credentials,
                                     Collection<? extends GrantedAuthority> authorities,
                                     String ipAddress, String userAgent) {
        super(principal, credentials, authorities);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}