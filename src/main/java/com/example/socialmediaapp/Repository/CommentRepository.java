package com.example.socialmediaapp.Repository;



import com.example.socialmediaapp.Models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    void deleteById(int id);
    List<Comment> findAllByUser_Id(int userId);
    List<Comment> findAllByPost_Id(int postId);
}