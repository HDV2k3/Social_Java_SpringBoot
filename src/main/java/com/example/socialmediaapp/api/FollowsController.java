package com.example.socialmediaapp.api;


import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Request.FollowRequest;
import com.example.socialmediaapp.Responses.FollowResponse;
import com.example.socialmediaapp.Service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
public class FollowsController {

    private final FollowService followService;

    public FollowsController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody FollowRequest followRequest){
        followService.add(followRequest);
        return new ResponseEntity<>("Followed", HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestBody FollowRequest  followRequest){
        followService.delete(followRequest);
        return new ResponseEntity<>("Unfollowed",HttpStatus.OK);
    }
    @GetMapping("/mutual/{userId}")
    public ResponseEntity<List<FollowResponse>> getMutualFollowers(@PathVariable int userId) {
        List<FollowResponse> mutualFollowers = followService.getMutualFollowers(userId);
        return ResponseEntity.ok(mutualFollowers);
    }
}

