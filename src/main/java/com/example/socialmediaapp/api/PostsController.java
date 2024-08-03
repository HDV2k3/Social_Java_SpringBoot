package com.example.socialmediaapp.api;
import com.example.socialmediaapp.Repository.PostRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.PostAddRequest;
import com.example.socialmediaapp.Responses.ApiResponse;
import com.example.socialmediaapp.Responses.PostGetResponse;
import com.example.socialmediaapp.Service.PostService;
import com.example.socialmediaapp.Service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.*;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final PostService postService;

    public PostsController(PostService postService, PostRepository postRepository, UserRepository userRepository, StorageService storageService) {
        this.postService = postService;
    }


    //main
    @GetMapping("/getallbyuser/{userId}")
    public ResponseEntity<List<PostGetResponse>> getPostsByUserId(@PathVariable int userId) {
        List<PostGetResponse> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    //main
    @GetMapping("/post-of-followed/{userId}")
    public ResponseEntity<List<PostGetResponse>> getPostsByFollowedUsers(@PathVariable int userId) {
        List<PostGetResponse> posts = postService.getPostsByFollowedUsers(userId);
        return ResponseEntity.ok(posts);
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> addPost(
            @RequestPart("post") PostAddRequest postAddRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
        postService.addPost(postAddRequest, files);
        return ResponseEntity.ok(new ApiResponse<>("Post added successfully", true));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam int id) {
        postService.delete(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }

}
