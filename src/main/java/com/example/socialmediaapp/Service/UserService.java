package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Mappers.UserMapper;
import com.example.socialmediaapp.Models.Follow;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.FollowRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.UserAddRequest;
import com.example.socialmediaapp.Responses.UserFollowingResponse;
import com.example.socialmediaapp.Responses.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;
    @Autowired
    public UserService(UserMapper userMapper, UserRepository userRepository, FollowRepository followRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.followRepository = followRepository;
    }

    public List<UserResponse> getAll(){

        return userMapper.usersToResponses(userRepository.findAll());
    }
    public UserResponse getResponseById(int id){
        User user = userRepository.findById(id).orElse(null);
        return userMapper.userToResponse(user);
    }
    public UserResponse getByEmail(String email){
        User user = userRepository.findByEmail(email);
        return userMapper.userToResponse(user);
    }

    public List<UserFollowingResponse> getUserFollowing(int userId){
        return userMapper.followsToFollowingResponses(followRepository.findAllByUser_Id(userId));
    }

    public boolean isFollowing(int userId,int followingId){
        Optional<Follow> follow = followRepository.findByUser_IdAndFollowing_Id(userId,followingId);
        return follow.isPresent();
    }

    public User getById(int id){
        return userRepository.findById(id).get();
    }
    public void add(UserAddRequest userAddRequest){
        User user = userMapper.requestToUser(userAddRequest);
        userRepository.save(user);
    }

    public void delete(int id){
        userRepository.deleteById(id);
    }
    public List<UserResponse> searchUsers(String query) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(query);
        return userMapper.usersToResponses(users);
    }
}
