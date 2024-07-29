package com.example.socialmediaapp.Service;

import com.example.socialmediaapp.Contants.URL_BUCKET_NAME;
import com.example.socialmediaapp.Models.Comment;
import com.example.socialmediaapp.Models.Post;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserImage;
import com.example.socialmediaapp.Repository.CommentRepository;
import com.example.socialmediaapp.Repository.PostRepository;
import com.example.socialmediaapp.Repository.UserImageRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.CommentAddRequest;
import com.example.socialmediaapp.Responses.CommentGetResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public  class CommentService {

    private final CommentRepository commentRepository;


    private final PostRepository postRepository;
    private final UserImageRepository userImageRepository;
    private final StorageService storageService;
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserImageRepository userImageRepository, StorageService storageService, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userImageRepository = userImageRepository;
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    private final UserRepository userRepository;
    // add comment
    public void addComment(CommentAddRequest request) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setDescription(request.getDescription());
        comment.setCreate_at(LocalDateTime.now());
        commentRepository.save(comment);
    }
    // get all comment -> postId
    public List<CommentGetResponse> getCommentsByPostId(int commentId) {

        return commentRepository.findAllByPost_Id(commentId).stream()
                .map(comment -> {
                    int userId = comment.getUser().getId();
                    UserImage userImage = userImageRepository.findByUser_Id(userId)
                            .orElseThrow(() -> new RuntimeException("Profile image not found for user: " + userId));
                    // Construct the full path to the image in Firebase Storage
                    String fullPath = URL_BUCKET_NAME.AVATAR_FOLDER + userImage.getUrl();

                    // Get a signed URL for the image
                    String avatar;
                    try {
                        avatar = storageService.getSignedUrl(URL_BUCKET_NAME.BUCKET_NAME, fullPath);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                return new CommentGetResponse(
                        comment.getId(),
                        comment.getPost().getId(),
                        comment.getUser().getId(),
                        comment.getUser().getName(),
                        comment.getUser().getLastName(),
                        avatar,
                        comment.getDescription(),
                        comment.getCreate_at()

                );
                }).collect(Collectors.toList());
    }
}