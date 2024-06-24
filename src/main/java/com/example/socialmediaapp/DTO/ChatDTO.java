package com.example.socialmediaapp.DTO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDTO {
    private Long id;
    private int senderId;
    private int receiverId;
    private String message;
    private LocalDateTime sentAt;
}