package com.example.socialmediaapp.Request;

import lombok.Data;

@Data
public class DecryptionRequest {
    private String encryptedMessage;
    private String privateKey;
}
