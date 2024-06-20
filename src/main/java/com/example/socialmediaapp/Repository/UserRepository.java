package com.example.socialmediaapp.Repository;


import com.example.socialmediaapp.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByNameContainingIgnoreCase(String name);

    void deleteById(int id);
    User findByEmail(String email);

}