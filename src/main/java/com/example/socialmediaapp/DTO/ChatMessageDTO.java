package com.example.socialmediaapp.DTO;


import com.example.socialmediaapp.Models.MessageType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ChatMessageDTO {
    private int senderId;
    private int receiverId;
    private String content;
    private String urlFile;
    private String messageType;
}