package com.example.socialmediaapp.Repository;



import com.example.socialmediaapp.Models.Post;
import com.example.socialmediaapp.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByUser_IdOrderByIdDesc(int userId);
    void deleteById(int id);
    List<Post> findAllByUser_IdInOrderByIdDesc(List<Integer> userIds);
}