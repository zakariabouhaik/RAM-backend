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

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("contact.bouhaikzakaria@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

        emailSender.send(message);
    }
}