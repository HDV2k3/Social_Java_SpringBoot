package com.example.socialmediaapp.Repository;


import com.example.socialmediaapp.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    void deleteById(int id);
    User findByEmail(String email);

}