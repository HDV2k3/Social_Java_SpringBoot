package com.example.socialmediaapp.Service;

import com.example.socialmediaapp.Models.EncryptionKey;
import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Repository.EncryptionKeyRepository;
import com.example.socialmediaapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class EncryptionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionKeyRepository encryptionKeyRepository;

    public EncryptionKey generateKeysForUser(int userId) throws NoSuchAlgorithmException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        // Generate RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());

        EncryptionKey encryptionKey = new EncryptionKey();
        encryptionKey.setUser(user);
        encryptionKey.setPublicKey(publicKey);
        encryptionKey.setPrivateKey(privateKey);
        encryptionKey.setCreatedAt(LocalDateTime.now());
        encryptionKey.setUpdatedAt(LocalDateTime.now());

        return encryptionKeyRepository.save(encryptionKey);
    }

    public Optional<String> getPublicKeyForUser(int userId) {
        return encryptionKeyRepository.findByUserId(userId)
                .map(EncryptionKey::getPublicKey);
    }

    public Optional<String> getPrivateKeyForUser(int userId) {
        return encryptionKeyRepository.findByUserId(userId)
                .map(EncryptionKey::getPrivateKey);
    }

    // Encrypt a message using the RSA public key
    public String encryptMessage(String message, String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString); // Decode the public key from Base64
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes); // Create key specification
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Initialize key factory for RSA
        PublicKey publicKey = keyFactory.generatePublic(keySpec); // Generate the public key

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Initialize cipher for RSA with padding
        cipher.init(Cipher.ENCRYPT_MODE, publicKey); // Set cipher to encryption mode with the public key
        byte[] encryptedBytes = cipher.doFinal(message.getBytes()); // Encrypt the message
        return Base64.getEncoder().encodeToString(encryptedBytes); // Encode the encrypted message in Base64
    }

    public String encryptMessageForSender(String message, String publicKey) throws Exception {
        return encryptMessage(message, publicKey);
    }

    public String encryptMessageForReceiver(String message, String publicKey) throws Exception {
        return encryptMessage(message, publicKey);
    }

    // Decrypt a message using the RSA private key
    public String decryptMessage(String encryptedMessage, String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString); // Decode the private key from Base64
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes); // Create key specification
        KeyFactory keyFactory = KeyFactory.getInstance("RSA"); // Initialize key factory for RSA
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec); // Generate the private key

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding"); // Initialize cipher for RSA with padding
        cipher.init(Cipher.DECRYPT_MODE, privateKey); // Set cipher to decryption mode with the private key
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage)); // Decrypt the message
        return new String(decryptedBytes); // Convert the decrypted bytes to string
    }
}