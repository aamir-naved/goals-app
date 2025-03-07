package com.accountability.accountability_app.controller;

import com.accountability.accountability_app.dto.ReportIssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report-issue")
public class ReportIssueController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendReport(@RequestBody ReportIssueRequest request) {
        try {
            System.out.println("📩 Inside sendReport method of ReportIssueController");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("aamirnaved0020@gmail.com");  // Your email
            message.setSubject("Issue Reported: " + request.getSubject());
            message.setText(
                    "📌 Issue Reported by: " + request.getName() + "\n" +
                            "📧 Email: " + request.getEmail() + "\n\n" +
                            "📝 Message:\n" + request.getMessage()
            );

            mailSender.send(message);
            System.out.println("✅ Mail sent successfully!");
            return ResponseEntity.ok("Issue reported successfully!");
        } catch (Exception e) {
            System.out.println("❌ Error while sending email: " + e.getMessage());
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }
}
