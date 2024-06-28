package com.example.socialmediaapp.api;

import com.example.socialmediaapp.DTO.ChatDTO;
import com.example.socialmediaapp.DTO.ChatMessageDTO;
import com.example.socialmediaapp.DTO.StatusDTO;
import com.example.socialmediaapp.DTO.TypingDTO;
import com.example.socialmediaapp.Models.*;
import com.example.socialmediaapp.Service.ChatService;
import com.example.socialmediaapp.Service.EncryptionService;
import com.example.socialmediaapp.Service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private UserStatusService userStatusService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO, Principal principal) throws Exception {

        // Lấy khóa công khai của người nhận
        String receiverPublicKey  = encryptionService.getPublicKeyForUser(chatMessageDTO.getReceiverId())
                .orElseGet(() -> {
                    // Nếu người nhận chưa có khóa, tạo mới khóa cho người nhận
                    EncryptionKey newKey = null;
                    try {
                        newKey = encryptionService.generateKeysForUser(chatMessageDTO.getReceiverId());
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    return newKey.getPublicKey();
                });

        String senderPublicKey   = encryptionService.getPublicKeyForUser(chatMessageDTO.getSenderId())
                .orElseGet(() -> {
                    // Nếu người nhận chưa có khóa, tạo mới khóa cho người nhận
                    EncryptionKey newKey = null;
                    try {
                        newKey = encryptionService.generateKeysForUser(chatMessageDTO.getReceiverId());
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                    return newKey.getPublicKey();
                });

        // Mã hóa tin nhắn cho người nhận
        String encryptedMessageForReceiver = encryptionService.encryptMessageForReceiver(chatMessageDTO.getContent(), receiverPublicKey);

        // Mã hóa tin nhắn cho người gửi
        String encryptedMessageForSender = encryptionService.encryptMessageForSender(chatMessageDTO.getContent(), senderPublicKey);

        // Lưu tin nhắn đã mã hóa vào hệ thống
        Chat chat = chatService.sendMessage(chatMessageDTO, encryptedMessageForReceiver, encryptedMessageForSender);
        ChatDTO chatDTO = convertToDTO(chat);

        // Tạo một topic chung cho cả hai người dùng
        String chatTopic = String.format("/topic/private-chat-%d-%d",
                Math.min(chatMessageDTO.getSenderId(), chatMessageDTO.getReceiverId()),
                Math.max(chatMessageDTO.getSenderId(), chatMessageDTO.getReceiverId()));

        messagingTemplate.convertAndSend(chatTopic, chatDTO);
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingDTO typingDTO) {
        long senderId = typingDTO.getSenderId();
        long receiverId = typingDTO.getReceiverId();

        // Chỉ gửi thông báo đến người nhận
        String typingTopic = String.format("/topic/typing-%d-%d", receiverId, senderId);

        messagingTemplate.convertAndSend(typingTopic, typingDTO);
    }


    @GetMapping("/history")
    public ResponseEntity<List<ChatDTO>> getChatHistory(@RequestParam int senderId, @RequestParam int receiverId) {
        List<Chat> chatHistory = chatService.getChatHistoryBetweenUsers(senderId, receiverId);
        List<ChatDTO> chatDTOs = chatHistory.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatDTOs);
    }

    @PutMapping("/{chatId}/status")
    public ResponseEntity<ChatDTO> updateMessageStatus(@PathVariable int chatId, @RequestParam MessageStatus status) {
        Chat updatedChat = chatService.updateMessageStatus(chatId, status);
        StatusDTO returnStatus = new StatusDTO();
        returnStatus.setId(chatId);
        returnStatus.setStatus(status.toString());
        // Send real-time notification about message status update
        String messageStatusTopic = String.format("/topic/message-status-%d", updatedChat.getId());
        messagingTemplate.convertAndSend(messageStatusTopic, returnStatus);

        return ResponseEntity.ok().build();
    }



    @PutMapping("/mark-delivered/{userId}")
    public ResponseEntity<Void> markMessagesAsDelivered(@PathVariable int userId) {
        chatService.markMessagesAsDelivered(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread")
    public ResponseEntity<Integer> getUnreadMessageCount(@RequestParam int userId) {
        int count = chatService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(count);
    }

    private ChatDTO convertToDTO(Chat chat) {
        ChatDTO dto = new ChatDTO();
        dto.setId(chat.getId());
        dto.setSenderId(chat.getSender().getId());
        dto.setReceiverId(chat.getReceiver().getId());
        dto.setMessage(chat.getMessage());
        dto.setMessageSender(chat.getSenderEncryptedMessage());
        dto.setMessageType(chat.getMessageType());
        dto.setStatus(chat.getStatus());
        dto.setSentAt(chat.getSentAt());
        dto.setDeliveredAt(chat.getDeliveredAt());
        dto.setReadAt(chat.getReadAt());
        dto.setUrlFile(chat.getFileUrl());
        return dto;
    }
}