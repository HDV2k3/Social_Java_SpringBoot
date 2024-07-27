package com.example.socialmediaapp.api;


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
import com.example.socialmediaapp.Service.JwtUtilService;
import com.example.socialmediaapp.Service.UserService;
import com.example.socialmediaapp.Contants.PredefinedRole;

import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;

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

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);
//        User savedUser = userRepository.save(user);
        // save new user in database and check exception
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
