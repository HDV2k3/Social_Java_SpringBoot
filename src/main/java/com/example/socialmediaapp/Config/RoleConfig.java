package com.example.socialmediaapp.Config;


import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
public class RoleConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository)
    {
        return  args -> {
            if (userRepository.findByNameContainingIgnoreCase("admin").isEmpty())
            {
                var roles = new HashSet<String>();
                roles.add(Role.ADMIN.name());
                User user = new User();
                user.setEmail("admin@gmail.com");
                user.setName("admin");
                user.setLastName("admin");
                user.setPassword(passwordEncoder.encode("admin"));
                user.setRoles(roles);
                userRepository.save(user);
                log.warn("Admin create successfully with email:admin@gmail.com and password:admin");
            }
        };
    }
}
