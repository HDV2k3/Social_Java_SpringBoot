package com.example.socialmediaapp.Events;


import com.example.socialmediaapp.Models.User;
import com.example.socialmediaapp.Service.EmailService;
import com.example.socialmediaapp.Service.VerificationTokenService;
import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;



@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private VerificationTokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        tokenService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String confirmationUrl = "http://localhost:3000/email-verification?token=" + token;

        try {
            emailService.sendVerificationEmail(recipientAddress, confirmationUrl);
        } catch (MessagingException e) {
            // Handle the exception (log it, notify the user, etc.)
            e.printStackTrace();
        }
    }
}
