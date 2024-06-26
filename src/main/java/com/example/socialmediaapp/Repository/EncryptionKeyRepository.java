package com.example.socialmediaapp.Repository;

import com.example.socialmediaapp.Models.EncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, Integer> {
    Optional<EncryptionKey> findByUserId(int userId);
}