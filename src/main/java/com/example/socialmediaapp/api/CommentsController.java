package com.example.socialmediaapp.api;


import com.example.socialmediaapp.Models.Comment;
import com.example.socialmediaapp.Request.CommentAddRequest;
import com.example.socialmediaapp.Responses.ApiResponse;
import com.example.socialmediaapp.Responses.CommentGetResponse;
import com.example.socialmediaapp.Responses.PostGetResponse;
import com.example.socialmediaapp.Service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService){
        this.commentService = commentService;
    }

    @PostMapping("/add")
    public  ResponseEntity<ApiResponse<Void>> addComment(@RequestBody CommentAddRequest request) {
             commentService.addComment(request);
        return ResponseEntity.ok(new ApiResponse<>("Comment added successfully", true));
    }
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentGetResponse>> getCommentsByPostId(@PathVariable int postId) {
        List<CommentGetResponse> comments = commentService.getCommentsByPostId(postId);

        return ResponseEntity.ok(comments);
    }
}
