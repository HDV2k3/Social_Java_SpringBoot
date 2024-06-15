package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.FollowMapper;
import com.example.socialmediaapp.Models.Follow;
import com.example.socialmediaapp.Repository.FollowRepository;
import com.example.socialmediaapp.Request.FollowRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



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


}
