package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.Models.Chat;
import com.example.socialmediaapp.Models.User;

import com.example.socialmediaapp.Repository.ChatRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public Chat sendMessage(int senderId, int receiverId, String message) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setReceiver(receiver);
        chat.setMessage(message);
        chat.setSentAt(LocalDateTime.now());
        chat.setCreateAt(LocalDateTime.now());

        return chatRepository.save(chat);
    }

    public List<Chat> getChatHistoryBetweenUsers(int userId1, int userId2) {
        List<Chat> sentMessages = chatRepository.findBySenderIdAndReceiverIdOrderBySentAtAsc(userId1, userId2);
        List<Chat> receivedMessages = chatRepository.findBySenderIdAndReceiverIdOrderBySentAtAsc(userId2, userId1);

        List<Chat> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);

        allMessages.sort(Comparator.comparing(Chat::getSentAt));

        return allMessages;
    }


}