package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.FollowMapper;
import com.example.socialmediaapp.Models.Follow;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.FollowRepository;
import com.example.socialmediaapp.Request.FollowRequest;
import com.example.socialmediaapp.Responses.FollowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final FollowMapper followMapper;
    private final UserService userService;
    @Autowired
    public FollowService(FollowRepository followRepository, FollowMapper followMapper, UserService userService) {
        this.followRepository = followRepository;
        this.followMapper = followMapper;
        this.userService = userService;
    }

    public void add(FollowRequest followAddRequest){
        if (userService.isFollowing(followAddRequest.getUserId(), followAddRequest.getFollowingId())){
            return;
        }
        followRepository.save(followMapper.addRequestToFollow(followAddRequest));
    }

    public  void delete(FollowRequest followRequest){
        Follow follow
                = followRepository.findByUser_IdAndFollowing_Id(followRequest.getUserId(), followRequest.getFollowingId()).orElse(null);
        followRepository.delete(follow);
    }
    // lấy ra tất cả user là bạn bè
public List<FollowResponse> getMutualFollowers(int userId) {
    List<Follow> following = followRepository.findByUserId(userId);
    List<Follow> followers = followRepository.findByFollowingId(userId);

    List<Follow> mutualFollows = following.stream()
            .filter(f -> followers.stream().anyMatch(follower -> follower.getUser().getId() == f.getFollowing().getId()))
            .collect(Collectors.toList());
    return followMapper.followsToResponses(mutualFollows);
}
}
