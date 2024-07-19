package com.example.socialmediaapp.Repository;


import com.example.socialmediaapp.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByNameContainingIgnoreCase(String name);
    void deleteById(int id);
    User findByEmail(String email);
    Optional<User> findByName(String name);

}