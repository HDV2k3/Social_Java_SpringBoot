package com.example.socialmediaapp.Repository;
import com.example.socialmediaapp.Models.NewLocationToken;
import com.example.socialmediaapp.Models.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewLocationTokenRepository extends JpaRepository<NewLocationToken, Long> {

    NewLocationToken findByToken(String token);

    NewLocationToken findByUserLocation(UserLocation userLocation);

}