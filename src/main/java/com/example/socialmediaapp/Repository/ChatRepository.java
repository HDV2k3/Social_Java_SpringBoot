package com.example.socialmediaapp.Repository;

import com.example.socialmediaapp.Models.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findBySenderIdAndReceiverIdOrderBySentAtAsc(int senderId, int receiverId);
}