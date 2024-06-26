package com.example.socialmediaapp.DTO;

import lombok.Data;

@Data
public class AttachmentDTO {
    private String fileName;
    private String fileUrl;
    private long fileSize;
    private String fileType;
}
