package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Exception.AppException;
import com.example.socialmediaapp.Exception.ErrorCode;
import com.example.socialmediaapp.Models.InvalidatedToken;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.InvalidatedTokenRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.RefreshRequest;
import com.example.socialmediaapp.Responses.UserJwtResponse;
import com.google.api.client.util.Value;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtilService {
    private final String SECRET_KEY = "defaultsecretkeythatisatleastsixtyfourbitslong";
    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @Value("${jwt.refreshable-duration}")
    private long REFRESHABLE_DURATION;

    @Autowired
    public JwtUtilService(UserRepository userRepository, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.userRepository = userRepository;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//    }
private Claims extractAllClaims(String token) {
    System.out.println("Token received: " + token);

    if (token == null || token.isEmpty() || !token.contains(".")) {
        throw new IllegalArgumentException("Invalid JWT token: " + token);
    }

    try {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    } catch (MalformedJwtException e) {
        // Log the token causing the issue
        System.out.println("Malformed JWT Token: " + token);
        throw e; // Rethrow the exception to propagate it up the call stack
    }
}



    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(String username,int userId,String fullName,String role) {
        Map<String, Object> claims = new HashMap<>();
        UserJwtResponse userJwtResponse = new UserJwtResponse();
        userJwtResponse.setId(userId);
        userJwtResponse.setEmail(username);
        userJwtResponse.setFullName(fullName);
        userJwtResponse.setRoles(role);
        claims.put("user",userJwtResponse);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public UserJwtResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(request.getToken(), true);

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Vô hiệu hóa token cũ
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jti)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        String username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        // Tạo token mới
        String newToken = generateToken(user.getName(), user.getId(), user.getLastName(), user.getRoles().toString());

        UserJwtResponse response = new UserJwtResponse();
        response.setToken(newToken);
        response.setId(user.getId());
        response.setEmail(user.getName());
        response.setFullName(user.getLastName());
        response.setRoles(user.getRoles().toString());
        return response;
    }

    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh
                ? Date.from(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS))
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        boolean notExpired = expiryTime.after(new Date());
        boolean notInvalidated = !invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());

        if (!(verified && notExpired && notInvalidated)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
}
