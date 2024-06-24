package com.example.socialmediaapp.Repository;

import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Models.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
    UserLocation findByCountryAndUser(String country, User user);
}