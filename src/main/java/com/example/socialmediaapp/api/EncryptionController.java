package com.example.socialmediaapp.api;

import com.example.socialmediaapp.Models.EncryptionKey;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.UserRepository;
import com.example.socialmediaapp.Request.DecryptionRequest;
import com.example.socialmediaapp.Security.SecurityUtils;
import com.example.socialmediaapp.Service.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/encryption")
@CrossOrigin(origins = "http://localhost:3000")
public class EncryptionController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/keys/{userId}")
    public ResponseEntity<EncryptionKey> generateKeys(@PathVariable int userId) throws Exception {
        EncryptionKey key = encryptionService.generateKeysForUser(userId);
        return ResponseEntity.ok(key);
    }

    @GetMapping("/keys/{userId}")
    public ResponseEntity<String> getPublicKey(@PathVariable int userId) {
        return encryptionService.getPublicKeyForUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/keys")
    public ResponseEntity<String> getPrivateKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName());
        return encryptionService.getPrivateKeyForUser(user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/encrypt")
    public ResponseEntity<String> encryptMessage(@RequestParam String message, @RequestParam String publicKey) throws Exception {

        String encryptedMessage = encryptionService.encryptMessage(message, publicKey);
        return ResponseEntity.ok(encryptedMessage);
    }

    @PostMapping("/decrypt")
    public ResponseEntity<String> decryptMessage(@RequestBody DecryptionRequest decryptionRequest) throws Exception {
        if(decryptionRequest.getEncryptedMessage() != null && decryptionRequest.getPrivateKey() != null) {
            String decryptedMessage = encryptionService.decryptMessage(decryptionRequest.getEncryptedMessage(), decryptionRequest.getPrivateKey());
            return ResponseEntity.ok(decryptedMessage);
        }
        return  ResponseEntity.ok(null);
    }
}
