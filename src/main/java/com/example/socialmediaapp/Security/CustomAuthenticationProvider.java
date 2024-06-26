package com.example.socialmediaapp.Security;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Service.DeviceService;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserRepository userRepository;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomAuthenticationToken authToken = (CustomAuthenticationToken) authentication;

        String username = authToken.getName();
        String password = authToken.getCredentials().toString();
        String ipAddress = authToken.getIpAddress();
        String userAgent = authToken.getUserAgent();

        UserDetails user = userDetailsService.loadUserByUsername(username);



        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        try {
            deviceService.verifyDevice(user, userRepository.findByEmail(authToken.getName()), ipAddress, userAgent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
        // If everything is okay, create a new fully authenticated token
        return new CustomAuthenticationToken(user, null, ipAddress, userAgent);
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }

    // Setters
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}