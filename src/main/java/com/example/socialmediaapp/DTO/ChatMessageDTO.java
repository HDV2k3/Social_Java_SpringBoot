package com.example.socialmediaapp.DTO;


import lombok.Data;

@Data
public class ChatMessageDTO {
    private int senderId;
    private int receiverId;
    private String content;
}