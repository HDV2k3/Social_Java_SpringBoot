
package com.example.socialmediaapp.Service;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class FirebaseStorageService {
    private static final String BUCKET_NAME = "chatappjava-a7ee2.appspot.com";

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        Storage storage = StorageClient.getInstance().bucket().getStorage();
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
        Blob blob = storage.create(blobInfo, file.getBytes());

        return blob.getName(); // Return the full file name (UUID + original name)
    }
    public String getSignedUrl(String storedFileName) throws IOException {
        Storage storage = StorageClient.getInstance().bucket().getStorage();

        // First, try to get the blob with the stored file name
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, storedFileName));

        if (blob == null) {
            // If not found, the stored name might be without UUID, so list all blobs and find a match
            Page<Blob> blobs = storage.list(BUCKET_NAME, Storage.BlobListOption.prefix(""));
            for (Blob b : blobs.iterateAll()) {
                if (b.getName().endsWith(storedFileName)) {
                    blob = b;
                    break;
                }
            }
        }

        if (blob == null) {
            throw new RuntimeException("File not found: " + storedFileName);
        }

        return blob.signUrl(1, TimeUnit.HOURS).toString();
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }
}