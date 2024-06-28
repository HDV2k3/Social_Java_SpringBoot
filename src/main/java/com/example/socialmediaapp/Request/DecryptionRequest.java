package com.example.socialmediaapp.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DecryptionRequest {
    @NotNull
    private String encryptedMessage;
    @NotNull
    private String privateKey;
}
