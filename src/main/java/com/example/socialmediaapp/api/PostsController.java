package com.example.socialmediaapp.api;



import com.example.socialmediaapp.Contants.URL_BUCKET_NAME;
import com.example.socialmediaapp.Models.Post;
import com.example.socialmediaapp.Models.PostImage;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.PostRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.PostAddRequest;
import com.example.socialmediaapp.Responses.ApiResponse;
import com.example.socialmediaapp.Responses.PostGetResponse;
import com.example.socialmediaapp.Service.FirebaseStorageService;
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
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final StorageService storageService;
    public PostsController(PostService postService, PostRepository postRepository, UserRepository userRepository, FirebaseStorageService firebaseStorageService, StorageService storageService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.storageService = storageService;
    }


    @GetMapping("/getall")
    public ResponseEntity<List<PostGetResponse>> getAll(){
        return new ResponseEntity<>(postService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/getbyid/{id}")
    public ResponseEntity<PostGetResponse> getById(@PathVariable int id){
        return new ResponseEntity<>(postService.getResponseById(id),HttpStatus.OK);
    }


@GetMapping("/getallbyuser/{userId}")
public ResponseEntity<List<PostGetResponse>> getPostsByUserId(@PathVariable int userId) {
    List<PostGetResponse> posts = postService.getPostsByUserId(userId);
    return ResponseEntity.ok(posts);
}
    @GetMapping("/getbyuserfollowing/{userId}")
    public ResponseEntity<List<PostGetResponse>> getAllByUserFollowing(@PathVariable int userId){
        return new ResponseEntity<>(postService.getByUserFollowing(userId),HttpStatus.OK);
    }

@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<ApiResponse<Void>> addPost(
        @RequestPart("post") PostAddRequest postAddRequest,
        @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {
    // Lấy thông tin user từ userId
    User user = userRepository.findById(postAddRequest.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
    Post post = new Post();
    post.setDescription(postAddRequest.getContentPost());
    post.setTitlePost(postAddRequest.getTitlePost());
    post.setUrlImagePost(postAddRequest.getUrlImagePost());
    post.setCreate_at(new Date());
    post.setUser(user);

    // Lưu các tệp hình ảnh nếu có
    if (files != null) {
        Set<PostImage> postImages = new HashSet<>();
        for (MultipartFile file : files) {
            String storedFileName = storageService.uploadFile(URL_BUCKET_NAME.BUCKET_NAME, URL_BUCKET_NAME.POST_FOLDER, file);
            PostImage postImage = new PostImage();

            postImage.setData(storedFileName.getBytes());
            postImage.setUrlImagePost(storedFileName);
            postImage.setPost(post);
            String contentType = file.getContentType();
            postImage.setType(contentType != null ? contentType : "unknown");
            postImages.add(postImage);
            postImage.setName(file.getOriginalFilename());
        }
        post.setPostImages(postImages);
    }

    // Lưu thông tin bài đăng vào cơ sở dữ liệu
    postRepository.save(post);

    return ResponseEntity.ok(new ApiResponse<>("Post added successfully", true));
}



    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam int id){
        postService.delete(id);
        return new ResponseEntity<>("Deleted",HttpStatus.OK);
    }

}
