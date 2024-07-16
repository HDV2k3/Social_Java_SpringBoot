package com.example.socialmediaapp.Config;


import com.example.socialmediaapp.Models.Role;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.RoleRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.constant.PredefinedRole;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleConfig {

     PasswordEncoder passwordEncoder;
    @NonFinal
    static final String ADMIN_USER_NAME = "admin";
    @NonFinal
    static final String ADMIN_USER_LASTNAME = "admin";
    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @NonFinal
    static final String ADMIN_EMAIL= "admin@gmail.com";
//    @Bean
//    ApplicationRunner applicationRunner(UserRepository userRepository)
//    {
//        return  args -> {
//            if (userRepository.findByNameContainingIgnoreCase("admin").isEmpty())
//            {
//                var roles = new HashSet<String>();
//                roles.add(Role.ADMIN.name());
//                User user = new User();
//                user.setEmail("admin@gmail.com");
//                user.setName("admin");
//                user.setLastName("admin");
//                user.setPassword(passwordEncoder.encode("admin"));
//                user.setRoles(roles);
//                userRepository.save(user);
//                log.warn("Admin create successfully with email:admin@gmail.com and password:admin");
//            }
//        };
//    }
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository)
    {
        log.info("Initializing application.....");
        return  args -> {
            if (userRepository.findByNameContainingIgnoreCase(ADMIN_USER_NAME).isEmpty())
            {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("USER ROLE")
                        .build());
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("ADMIN ROLE")
                        .build());
                var roles = new HashSet<Role>();
                roles.add(adminRole);
                User user = new User();
                user.setEmail(ADMIN_EMAIL);
                user.setName(ADMIN_USER_NAME);
                user.setLastName(ADMIN_USER_LASTNAME);
                user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
                user.setRoles(roles);
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
        };
    }
}
