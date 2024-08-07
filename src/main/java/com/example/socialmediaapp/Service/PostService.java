package com.example.socialmediaapp.Service;

import com.example.socialmediaapp.Contants.URL_BUCKET_NAME;
import com.example.socialmediaapp.Mappers.PostMapper;
import com.example.socialmediaapp.Models.Follow;
import com.example.socialmediaapp.Models.Post;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserImage;
import com.example.socialmediaapp.Repository.FollowRepository;
import com.example.socialmediaapp.Repository.PostRepository;
import com.example.socialmediaapp.Repository.UserImageRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Responses.PostGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;

    private final UserRepository userRepository;
    private final StorageService storageService;
    private final FollowRepository followRepository;
    private final UserImageRepository userImageRepository;

    @Autowired
    public PostService(PostRepository postRepository, PostMapper postMapper, UserRepository userRepository, StorageService storageService, FollowRepository followRepository, UserImageRepository userImageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.followRepository = followRepository;
        this.userImageRepository = userImageRepository;
    }


    public void delete(int id) {
        postRepository.deleteById(id);
    }

    // lay cac bai post cua cac friend
    public List<PostGetResponse> getPostsByFollowedUsers(int currentUserId) {
        // Get the list of users that the current user is following
        List<Follow> follows = followRepository.findAllByUser_Id(currentUserId);
        List<Integer> followedUserIds = follows.stream().map(follow -> follow.getFollowing().getId()).collect(Collectors.toList());

        if (followedUserIds.isEmpty()) {
            return Collections.emptyList(); // No followed users
        }
        // Get posts from followed users
        List<Post> posts = postRepository.findAllByUser_IdInOrderByIdDesc(followedUserIds);
        // Map the posts to PostGetResponse
        return posts.stream().map(post -> {
            int postUserId = post.getUser().getId();
            UserImage userImage = userImageRepository.findByUser_Id(postUserId).orElseThrow(() -> new RuntimeException("Profile image not found for user: " + postUserId));

            // full path to the image in Firebase Storage
            String fullPath = URL_BUCKET_NAME.AVATAR_FOLDER + userImage.getUrl();

            // Get a signed URL for the image
            String avatar;
            try {
                avatar = storageService.getSignedUrl(URL_BUCKET_NAME.BUCKET_NAME, fullPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<String> imageUrls = post.getPostImages().stream().map(postImage -> {
                try {
                    return storageService.getSignedUrl(URL_BUCKET_NAME.BUCKET_NAME, URL_BUCKET_NAME.POST_FOLDER + postImage.getUrlImagePost());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());
            return new PostGetResponse(
                    post.getId(),
                    post.getUser().getId(),
                    post.getUser().getName(),
                    post.getUser().getLastName(),
                    post.getDescription(),
                    post.getTitlePost(),
                    imageUrls,
                    post.getCreate_at(),
                    avatar);
        }).collect(Collectors.toList());
    }

    public List<PostGetResponse> getPostsByUserId(int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return postRepository.findAllByUser_IdOrderByIdDesc(userId).stream().map(post -> {
            UserImage userImage = userImageRepository.findByUser_Id(userId).orElseThrow(() -> new RuntimeException("Profile image not found for user: " + userId));

//        log.info("Found user image record: {}", userImage.getUrl());

            // Construct the full path to the image in Firebase Storage
            String fullPath = URL_BUCKET_NAME.AVATAR_FOLDER + userImage.getUrl();

            // Get a signed URL for the image
            String avatar;
            try {
                avatar = storageService.getSignedUrl(URL_BUCKET_NAME.BUCKET_NAME, fullPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            List<String> imageUrls = post.getPostImages().stream().map(postImage -> {
                try {
                    return storageService.getSignedUrl(URL_BUCKET_NAME.BUCKET_NAME, URL_BUCKET_NAME.POST_FOLDER + postImage.getUrlImagePost());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

            return new PostGetResponse(post.getId(), user.getId(), user.getName(), user.getLastName(), post.getDescription(), post.getTitlePost(), imageUrls, post.getCreate_at(), avatar);
        }).collect(Collectors.toList());
    }

}
