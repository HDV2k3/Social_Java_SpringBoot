package com.example.socialmediaapp.api;

import com.example.socialmediaapp.Request.UserAddRequest;
import com.example.socialmediaapp.Responses.UserResponse;
import com.example.socialmediaapp.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getall")
    public ResponseEntity<List<UserResponse>> getAll(){
        return new ResponseEntity<>(userService.getAll(),HttpStatus.OK);
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
}
