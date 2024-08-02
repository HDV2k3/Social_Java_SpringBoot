package com.example.socialmediaapp.api;


import com.example.socialmediaapp.Events.OnRegistrationCompleteEvent;
import com.example.socialmediaapp.Exception.AppException;
import com.example.socialmediaapp.Exception.ErrorCode;
import com.example.socialmediaapp.Mappers.UserMapper;
import com.example.socialmediaapp.Models.InvalidatedToken;
import com.example.socialmediaapp.Models.Role;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.InvalidatedTokenRepository;
import com.example.socialmediaapp.Repository.RoleRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Responses.ApiResponse;
import com.example.socialmediaapp.Request.LoginRequest;
import com.example.socialmediaapp.Request.LogoutRequest;
import com.example.socialmediaapp.Request.RegisterRequest;
import com.example.socialmediaapp.Security.CustomAuthenticationToken;
import com.example.socialmediaapp.Service.EmailService;
import com.example.socialmediaapp.Service.JwtUtilService;
import com.example.socialmediaapp.Service.UserService;
import com.example.socialmediaapp.Contants.PredefinedRole;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtilService jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final  RoleRepository roleRepository;
    private final  InvalidatedTokenRepository invalidatedTokenRepository;
    UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;
    public AuthController(AuthenticationManager authenticationManager, JwtUtilService jwtUtil, PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Create a custom authentication token that includes IP and User-Agent

            CustomAuthenticationToken authToken = new CustomAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword(),
                    loginRequest.getIpAddress(),
                    loginRequest.getUserAgent()
            );

            Authentication auth = authenticationManager.authenticate(authToken);

            User user = userRepository.findByEmail(loginRequest.getEmail());
            return new ResponseEntity<>(jwtUtil.generateToken(
                    user.getEmail(),
                    user.getId(),
                    user.getName() + " " + user.getLastName(),
                    user.getRoles().toString()
            ), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
//    @GetMapping("/verify-email")
//    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
//        String result = userService.validateVerificationToken(token);
//        if (result.equals("valid")) {
//            return new ResponseEntity<>("Email verified successfully!", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Invalid or expired verification token.", HttpStatus.BAD_REQUEST);
//        }
//    }

//    @GetMapping("/verify-email")
//    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
//        String result = userService.validateVerificationToken(token);
//        if (result.equals("valid")) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Location", "http://localhost:3000/login");
//            return new ResponseEntity<>("Email verified successfully! Redirecting to login page...", headers, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Invalid or expired verification token.", HttpStatus.BAD_REQUEST);
//        }
//    }
@GetMapping("/verify-email")
public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam("token") String token) {
    Map<String, Object> response = new HashMap<>();

    try {
        String result = userService.validateVerificationToken(token);

        if (result.equals("valid")) {
            String email = userService.getEmailByToken(token); // Retrieve email by token
            if (email != null) {
                response.put("email", email);
                response.put("message", "Email verified successfully! Redirecting to login page...");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "Invalid token.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            response.put("message", "Invalid or expired verification token.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    } catch (Exception e) {
        response.put("message", "Error verifying email.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


//    @PostMapping("/resend-verification")
//    public ResponseEntity<String> resendVerification(@RequestParam("email") String email) {
//        User user = userService.findUserByEmail(email);
//        if (user == null) {
//            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//        }
//
//        if (user.isEnabled()) {
//            return new ResponseEntity<>("User is already verified", HttpStatus.BAD_REQUEST);
//        }
//
//        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
//        return new ResponseEntity<>("Verification email resent", HttpStatus.OK);
//    }
@PostMapping("/resend-verification")
public ResponseEntity<String> resendVerification(@RequestParam("token") String token) {
    try {
        String email = userService.getEmailByToken(token);
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        if (user.isEnabled()) {
            return new ResponseEntity<>("User is already verified", HttpStatus.BAD_REQUEST);
        }

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return new ResponseEntity<>("Verification email resent", HttpStatus.OK);
    } catch (RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request){
        // kiểm tra email đã tồn tại hay chưa
        if (userRepository.findByEmail(registerRequest.getEmail())!=null){
            return new ResponseEntity<>("Email already exist",HttpStatus.BAD_REQUEST);
        }
        // create new object User
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setLastName(registerRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(false);
        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
        // save new user in database and check exception
        // Generate verification token and send email
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        userService.addUserLocation(user, registerRequest.getIpAddress());

        return new ResponseEntity<>(jwtUtil.generateToken(
                registerRequest.getEmail(),
                userRepository.findByEmail(registerRequest.getEmail()).getId(),
                registerRequest.getName() +" "+registerRequest.getLastName(),registerRequest.getRole()
        )
                ,HttpStatus.OK
        );
    }
    //        String confirmationUrl = "http://localhost:3000/email-verification?token=" + token;
//        emailService.sendEmail(user.getEmail(), "Email Verification", "Click the link to verify your email: " + confirmationUrl);
    private final String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = jwtUtil.verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception) {
            log.info("Token already expired");
        }
        return ApiResponse.<Void>builder().build();
    }
}
