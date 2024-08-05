package com.example.rambackend.servicesImpl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("contact.bouhaikzakaria@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // Enable HTML content

        ByteArrayResource attachmentResource = new ByteArrayResource(attachment);
        helper.addAttachment(attachmentName, attachmentResource);

        emailSender.send(message);
    }
}