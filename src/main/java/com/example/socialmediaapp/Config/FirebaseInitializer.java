package com.example.socialmediaapp.Config;

import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseInitializer {
    public static void initialize() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(
                        new FileInputStream("src/main/resources/chatappjava-a7ee2-firebase-adminsdk-aqeah-d133e3e853.json"))
                .createScoped(Lists.newArrayList());

        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build();

        Storage storage = storageOptions.getService();
        // Ensure storage is initialized and authorized
    }
}
