package com.example.socialmediaapp.Repository;




import com.example.socialmediaapp.Models.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Integer> {
    Optional<PostImage> findPostImageByPost_Id(int postId);
}