package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Contants.URL_BUCKET_NAME;
import com.example.socialmediaapp.Mappers.PostMapper;
import com.example.socialmediaapp.Models.Post;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.PostRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.PostAddRequest;
import com.example.socialmediaapp.Responses.PostGetResponse;
import com.example.socialmediaapp.Responses.UserFollowingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {


    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private  final FirebaseStorageService firebaseStorageService;
    private  final  StorageService storageService;
    @Autowired
    public PostService(PostRepository postRepository, PostMapper postMapper, UserService userService, UserRepository userRepository, FirebaseStorageService firebaseStorageService, StorageService storageService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.storageService = storageService;
    }

    public List<PostGetResponse> getAll(){
        List<Post> posts = postRepository.findAll();
        return  postMapper.postsToGetResponses(posts);
    }

    public PostGetResponse getResponseById(int id){
        Post post = postRepository.findById(id).orElse(null);
        return postMapper.postToGetResponse(post);
    }

    public Post getById(int id){
        return postRepository.findById(id).get();
    }

    public List<PostGetResponse> getAllByUser(int userId){
        List<Post> userPosts = postRepository.findAllByUser_IdOrderByIdDesc(userId);
        return postMapper.postsToGetResponses(userPosts);
    }

    public List<PostGetResponse> getByUserFollowing(int userId){
        List<UserFollowingResponse> follows = userService.getUserFollowing(userId);
        List<Post> set = new ArrayList<>();

        for(UserFollowingResponse user : follows){
            set.addAll(postRepository.findAllByUser_IdOrderByIdDesc(user.getUserId()));
        }

        set.sort(Comparator.comparing(Post::getId).reversed());

        return postMapper.postsToGetResponses(set);
    }

    public int add(PostAddRequest postAddRequest){
        Post post =  postMapper.postAddRequestToPost(postAddRequest);
        postRepository.save(post);
        return post.getId();
    }

    public void delete(int id){
        postRepository.deleteById(id);
    }

//    public List<PostGetResponse> getPostsByUserId(int userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return postRepository.findAllByUser_IdOrderByIdDesc(userId).stream()
//                .map(post -> {
//                    List<String> imageUrls = post.getPostImages().stream()
//                            .map(postImage -> {
//                                try {
//                                    return firebaseStorageService.getSignedUrl(postImage.getName());
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            })
//                            .collect(Collectors.toList());
//
//
//                    return new PostGetResponse(
//                            post.getId(),
//                            user.getId(),
//                            user.getName(),
//                            user.getLastName(),
//                            post.getDescription(),
//                            post.getTitlePost(),
//                            imageUrls,
//                            post.getCreate_at()
//                    );
//                }).collect(Collectors.toList());
//    }
public List<PostGetResponse> getPostsByUserId(int userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    return postRepository.findAllByUser_IdOrderByIdDesc(userId).stream()
            .map(post -> {
                List<String> imageUrls = post.getPostImages().stream()
                        .map(postImage -> {
                            try {
                                return storageService.getSignedUrl(URL_BUCKET_NAME.BUCKET_NAME, URL_BUCKET_NAME.POST_FOLDER + postImage.getUrlImagePost());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());

                return new PostGetResponse(
                        post.getId(),
                        user.getId(),
                        user.getName(),
                        user.getLastName(),
                        post.getDescription(),
                        post.getTitlePost(),
                        imageUrls,
                        post.getCreate_at()
                );
            }).collect(Collectors.toList());
}

}
