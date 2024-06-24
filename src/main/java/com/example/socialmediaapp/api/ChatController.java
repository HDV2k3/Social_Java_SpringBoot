package com.example.socialmediaapp.api;


import com.example.socialmediaapp.DTO.ChatDTO;
import com.example.socialmediaapp.DTO.ChatMessageDTO;
import com.example.socialmediaapp.Mappers.ChatMapper;
import com.example.socialmediaapp.Models.Chat;

import com.example.socialmediaapp.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        Chat chat = chatService.sendMessage(chatMessageDTO.getSenderId(), chatMessageDTO.getReceiverId(), chatMessageDTO.getContent());
        ChatDTO chatDTO = chatMapper.chatToDTO(chat);

        // Gửi tin nhắn đến người nhận cụ thể
        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessageDTO.getReceiverId()),
                "/topic/private",
                chatDTO
        );

        return chatDTO;
    }

    @GetMapping("/api/chat/history")
    public List<ChatDTO> getChatHistory(@RequestParam int senderId, @RequestParam int receiverId) {
        List<Chat> chatHistory = chatService.getChatHistoryBetweenUsers(senderId, receiverId);
        return chatHistory.stream()
                .map(chatMapper::chatToDTO)
                .collect(Collectors.toList());
    }


}