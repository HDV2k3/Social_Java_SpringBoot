package com.example.socialmediaapp.Service;

import com.example.socialmediaapp.Contants.PredefinedRole;
import com.example.socialmediaapp.Events.OnRegistrationCompleteEvent;
import com.example.socialmediaapp.Exception.AppException;
import com.example.socialmediaapp.Exception.ErrorCode;
import com.example.socialmediaapp.Models.InvalidatedToken;
import com.example.socialmediaapp.Models.Role;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.InvalidatedTokenRepository;
import com.example.socialmediaapp.Repository.RoleRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.LoginRequest;
import com.example.socialmediaapp.Request.RegisterRequest;
import com.example.socialmediaapp.Security.CustomAuthenticationToken;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtilService jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private VerificationTokenService validateVerificationToken;

    @Autowired
    private EmailService emailService;

    public Map<String, String> login(LoginRequest loginRequest) throws MessagingException {
        CustomAuthenticationToken authToken = new CustomAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                loginRequest.getIpAddress(),
                loginRequest.getUserAgent()
        );

        Authentication auth = authenticationManager.authenticate(authToken);
        User user = userRepository.findByEmail(loginRequest.getEmail());

        Map<String, String> response = new HashMap<>();

        if (!user.isEnabled()) {
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
            response.put("message", "Registration successful. Please check your email to verify your account.");
            response.put("verificationToken", user.getVerificationToken());
        } else {
            String jwtToken = jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getName() + " " + user.getLastName(),
                    user.getRoles().toString()
            );
            response.put("jwtToken", jwtToken);
        }

        return response;
    }

    public Map<String, Object> verifyEmail(String token) {
        Map<String, Object> response = new HashMap<>();
        String result = validateVerificationToken.validateVerificationToken(token);

        if ("valid".equals(result)) {
            String email = userService.getEmailByToken(token);
            if (email != null) {
                response.put("email", email);
                response.put("message", "Email verified successfully! You can now log in.");
            } else {
                response.put("message", "Invalid token.");
            }
        } else {
            response.put("message", "Invalid or expired verification token.");
        }

        return response;
    }

    public String resendVerification(String token) throws MessagingException {
        String email = userService.getEmailByToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "User not found";
        }

        if (user.isEnabled()) {
            return "User is already verified";
        }

        String confirmationUrl = "http://localhost:3000/succes-email-verification?token=" + token;
        emailService.sendVerificationEmail(email, user.getName(), user.getLastName(), confirmationUrl);
        return "Verification email resent";
    }

    public Map<String, String> register(RegisterRequest registerRequest) throws MessagingException {
        if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setLastName(registerRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(false);

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        userService.addUserLocation(user, registerRequest.getIpAddress());
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));

        String jwtToken = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getName() + " " + user.getLastName(),
                user.getRoles().stream().findFirst().map(Role::getName).orElse("USER")
        );

        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful. Please check your email to verify your account.");
        response.put("verificationToken", user.getVerificationToken());
        response.put("jwtToken", jwtToken);

        return response;
    }

    public void logout(String token) throws ParseException, JOSEException {
        var signToken = jwtUtil.verifyToken(token, true);
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);
    }
}
