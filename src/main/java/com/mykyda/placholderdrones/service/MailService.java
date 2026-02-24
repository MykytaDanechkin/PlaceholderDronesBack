package com.mykyda.placholderdrones.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    public void sendOrderConfirmationEmail(UUID orderId, String email) {
        System.out.println("Sending email to " + email + " " + orderId);
    }

    public void sendPaymentSuccessConfirmation(UUID orderId, String email) {
        System.out.println("Sending email to " + email + " " + orderId);
    }
}
