package com.anjaanvivek.artistwebsite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final WebClient webClient;

    public EmailService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.brevo.com/v3/smtp/email").build();
    }

    public void sendSimpleMail(String to, String subject, String body) {
        // Prepare JSON payload for Brevo
        Map<String, Object> emailRequest = Map.of(
            "sender", Map.of("name", "Anjaan's Art", "email", "vsameer1408@gmail.com"),
            "to", List.of(Map.of("email", to)),
            "subject", subject,
            "htmlContent", "<html><body><p>" + body + "</p></body></html>"
        );

        // Send API Request (Port 443 - Allowed on Railway)
        try {
            webClient.post()
                .header("api-key", brevoApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(emailRequest)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Wait for response
            
            System.out.println("✅ Email sent successfully via Brevo API to " + to);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email sending failed");
        }
    }
}



//package com.anjaanvivek.artistwebsite.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class EmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    /**
//     * Sends a simple text email with subject and body.
//     */
//    public void sendSimpleMail(String to, String subject, String body) {
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setTo(to);
//        msg.setSubject(subject);
//        msg.setText(body);
//        mailSender.send(msg);
//    }
//}