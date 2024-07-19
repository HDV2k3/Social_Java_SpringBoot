package com.example.socialmediaapp.api;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Request.ApiResponse;
import com.example.socialmediaapp.Request.UserAddRequest;
import com.example.socialmediaapp.Responses.UserJwtResponse;
import com.example.socialmediaapp.Responses.UserResponse;
import com.example.socialmediaapp.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
    @Autowired
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }
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


}
