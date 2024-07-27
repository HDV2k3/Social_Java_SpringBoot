package com.example.socialmediaapp.Service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
@Service
public class StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    public String uploadFile(String bucketName, String folderName, MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        String objectName = folderName + fileName; // Đã bao gồm "post/"
        Storage storage = StorageClient.getInstance().bucket(bucketName).getStorage();
        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        Blob blob = storage.create(blobInfo, file.getBytes());

        return fileName; // Chỉ trả về tên file, không bao gồm đường dẫn thư mục
    }
    public String getSignedUrl(String bucketName, String filePath) throws IOException {
        Storage storage = StorageClient.getInstance().bucket(bucketName).getStorage();
        BlobId blobId = BlobId.of(bucketName, filePath);
        Blob blob = storage.get(blobId);

        if (blob == null || !blob.exists()) {
            log.error("File not found: {}", filePath);

            return null; // hoặc throw một exception tùy chỉnh
        }

        return blob.signUrl(1, TimeUnit.HOURS).toString();
    }
    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }
}
