package com.example.socialmediaapp.Service;


import com.example.socialmediaapp.DTO.ChatMessageDTO;
import com.example.socialmediaapp.Models.*;

import com.example.socialmediaapp.Repository.ChatRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public Chat sendMessage(ChatMessageDTO chatMessageDTO, String encryptedMessageForReceiver, String encryptedMessageForSender) {
        User sender = userRepository.findById(chatMessageDTO.getSenderId()).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(chatMessageDTO.getReceiverId()).orElseThrow(() -> new RuntimeException("Receiver not found"));
        Chat chat = new Chat();

        if(Objects.equals(chatMessageDTO.getMessageType(), "TEXT")){
            chat.setMessageType(MessageType.TEXT);
            chat.setMessage(encryptedMessageForReceiver);
            chat.setSenderEncryptedMessage(encryptedMessageForSender);
        } else if(Objects.equals(chatMessageDTO.getMessageType(), "FILE")){
            chat.setMessageType(MessageType.FILE);
            chat.setFileUrl(chatMessageDTO.getUrlFile());
        } else if(Objects.equals(chatMessageDTO.getMessageType(), "IMAGE")) {
            chat.setMessageType(MessageType.IMAGE);
            chat.setFileUrl(chatMessageDTO.getUrlFile());
        }


        chat.setSender(sender);
        chat.setReceiver(receiver);

        chat.setStatus(MessageStatus.SENT);
        chat.setSentAt(LocalDateTime.now());
        chat.setCreateAt(LocalDateTime.now());


        Chat savedChat = chatRepository.save(chat);

        return savedChat;
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

    public Chat updateMessageStatus(int chatId, MessageStatus status) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        chat.setStatus(status);
        if (status == MessageStatus.DELIVERED) {
            chat.setDeliveredAt(LocalDateTime.now());
        } else if (status == MessageStatus.READ) {
            chat.setReadAt(LocalDateTime.now());
        }
        return chatRepository.save(chat);
    }

    public int getUnreadMessageCount(int userId) {
        return chatRepository.countByReceiverIdAndStatus(userId, MessageStatus.SENT);
    }

    public void markMessagesAsDelivered(int userId) {
        List<Chat> undeliveredMessages = chatRepository.findByReceiverIdAndStatus(userId, MessageStatus.SENT);
        for (Chat chat : undeliveredMessages) {
            chat.setStatus(MessageStatus.DELIVERED);
            chat.setDeliveredAt(LocalDateTime.now());
            chatRepository.save(chat);
        }
    }


}