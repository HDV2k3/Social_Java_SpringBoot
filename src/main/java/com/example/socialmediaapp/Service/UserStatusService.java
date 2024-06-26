package com.example.socialmediaapp.Service;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserStatus;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserStatusService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    public UserStatus updateUserStatus(int userId, UserStatus.Status status) {
        Optional<UserStatus> existingStatus = userStatusRepository.findByUserId(userId);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }

        UserStatus userStatus;

        if (existingStatus.isPresent()) {
            userStatus = existingStatus.get();
        } else {
            userStatus = new UserStatus();
            userStatus.setUser(user.get());
        }

        userStatus.setStatus(status);
        userStatus.setLastOnline(LocalDateTime.now());

        return userStatusRepository.save(userStatus);
    }



    public Optional<UserStatus> getUserStatus(int userId) {
        return userStatusRepository.findByUserId(userId);
    }
}