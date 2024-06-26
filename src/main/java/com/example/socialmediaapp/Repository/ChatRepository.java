package com.example.socialmediaapp.Repository;

import com.example.socialmediaapp.Models.Chat;
import com.example.socialmediaapp.Models.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    List<Chat> findBySenderIdAndReceiverIdOrderBySentAtAsc(int senderId, int receiverId);
    Chat findChatBySenderIdAndReceiverId(int senderId, int receiverId);
    int countByReceiverIdAndStatus(int receiverId, MessageStatus status);

    List<Chat> findByReceiverIdAndStatus(int userId, MessageStatus messageStatus);
}