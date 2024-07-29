package com.example.socialmediaapp.Repository;



import com.example.socialmediaapp.Models.Follow;
import com.example.socialmediaapp.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Integer> {
    List<Follow> findAllByUser_Id(int userId);
    Optional<Follow> findByUser_IdAndFollowing_Id(int userId,int followingId);

    List<Follow> findByFollowingId(int userId);
    List<Follow> findByUserId(int userId);

}