package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.PostMapper;
import com.example.socialmediaapp.Models.Post;
import com.example.socialmediaapp.Repository.PostRepository;
import com.example.socialmediaapp.Request.PostAddRequest;
import com.example.socialmediaapp.Responses.PostGetResponse;
import com.example.socialmediaapp.Responses.UserFollowingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserService userService;
    @Autowired
    public PostService(PostRepository postRepository, PostMapper postMapper, UserService userService) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.userService = userService;
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
}
