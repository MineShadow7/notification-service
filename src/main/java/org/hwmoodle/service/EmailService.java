package org.hwmoodle.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @CircuitBreaker(name = "emailServiceCB", fallbackMethod = "emailFallback")
    @TimeLimiter(name = "emailServiceCB")
    public CompletableFuture<String> sendEmail(String to, String subject, String text) {
        return CompletableFuture.supplyAsync(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            try {
                mailSender.send(message);
                return "Email sent successfully";
            } catch (Exception e) {
                throw new RuntimeException("Failed to send email", e);
            }
        });
    }

    public CompletableFuture<String> emailFallback(String to, String subject, String text, Exception e) {
        return CompletableFuture.completedFuture(
            "Email service is currently unavailable. Message will be retried later."
        );
    }
}

