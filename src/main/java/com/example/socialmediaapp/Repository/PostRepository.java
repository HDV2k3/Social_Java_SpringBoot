package com.example.socialmediaapp.Repository;



import com.example.socialmediaapp.Models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByUser_IdOrderByIdDesc(int userId);
    void deleteById(int id);
}