package com.example.socialmediaapp.api;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserImage;
import com.example.socialmediaapp.Repository.UserImageRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.UserAddRequest;
import com.example.socialmediaapp.Responses.ApiResponse;
import com.example.socialmediaapp.Responses.UserJwtResponse;
import com.example.socialmediaapp.Responses.UserResponse;
import com.example.socialmediaapp.Service.FirebaseStorageService;
import com.example.socialmediaapp.Service.UserService;
import org.apache.sshd.common.global.GlobalRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    @Autowired
    private final UserService userService;
    @Autowired
    private FirebaseStorageService firebaseStorageService;
    @Autowired
    private UserImageRepository userImageRepository;
    public UsersController(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    private UserRepository userRepository;
@GetMapping("/me")
public ResponseEntity<UserJwtResponse> getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
        String email = auth.getName(); // Email người dùng đăng nhập
        UserJwtResponse userJwtResponse = userService.getUserInfo(email);
        if (userJwtResponse != null) {
            return ResponseEntity.ok(userJwtResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}

@GetMapping("/getall")
public ResponseEntity<List<UserResponse>> getAll(){
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
        log.info("Name: {}", auth.getName());
        auth.getAuthorities().forEach(grantedAuthority ->
                log.info("Authority: {}", grantedAuthority.getAuthority())
        );
    } else {
        log.warn("No authentication information found");
    }
    return new ResponseEntity<>(userService.getAll(), HttpStatus.OK);
}
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable int id){
        return new ResponseEntity<>(userService.getResponseById(id),HttpStatus.OK);
    }

    @GetMapping("/isfollowing")
    public ResponseEntity<Boolean> isFollowing(@RequestParam int userId,@RequestParam int followingId){
        return new ResponseEntity<>(userService.isFollowing(userId,followingId),HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody UserAddRequest userAddRequest){
        userService.add(userAddRequest);
        return new ResponseEntity<>("User Added",HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam int id){
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam("query") String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<ApiResponse<Void>> uploadProfileImage(@PathVariable int userId, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String imageUrl = firebaseStorageService.uploadFile(file);

            UserImage userImage = new UserImage();
            userImage.setName(file.getOriginalFilename());
            userImage.setType(file.getContentType());
            userImage.setUrl(imageUrl);
            userImage.setUser(user);

            userImageRepository.save(userImage);

            return new ResponseEntity<>(new ApiResponse<>("Profile image uploaded successfully", true), HttpStatus.OK);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image: " + e.getMessage());
        }
    }
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<ApiResponse<String>> getProfileImage(@PathVariable int userId) {
        log.info("Fetching profile image for user ID: {}", userId);
        try {
            UserImage userImage = userImageRepository.findByUser_Id(userId)
                    .orElseThrow(() -> new RuntimeException("Profile image not found for user: " + userId));

            log.info("Found user image record: {}", userImage.getName());

            String signedUrl = firebaseStorageService.getSignedUrl(userImage.getName());
            log.info("Generated signed URL: {}", signedUrl);

            return new ResponseEntity<>(new ApiResponse<>(signedUrl, true), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Runtime error fetching profile image for user {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), false), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error("IO error fetching profile image for user {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>("Error accessing the image file: " + e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
