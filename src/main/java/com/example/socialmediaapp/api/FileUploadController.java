package com.example.socialmediaapp.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileUploadController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final SimpMessagingTemplate messagingTemplate;

    public FileUploadController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Save the file locally
            Path path = Paths.get(uploadDir + File.separator + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // Prepare the file information to send to the client
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("name", file.getOriginalFilename());
            fileInfo.put("type", file.getContentType());
            fileInfo.put("url", "http://localhost:8080/uploads/" + file.getOriginalFilename());
            // Send the file information via WebSocket
            messagingTemplate.convertAndSend("/topic/files", fileInfo);
            return ResponseEntity.ok(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed");
        }
    }
}
