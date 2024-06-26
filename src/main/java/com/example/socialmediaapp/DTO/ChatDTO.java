package com.example.socialmediaapp.DTO;


import com.example.socialmediaapp.Models.MessageStatus;
import com.example.socialmediaapp.Models.MessageType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatDTO {
    private int id;
    private int senderId;
    private int receiverId;
    private String message;
    private String messageSender;
    private MessageType messageType;
    private MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private String urlFile;
}