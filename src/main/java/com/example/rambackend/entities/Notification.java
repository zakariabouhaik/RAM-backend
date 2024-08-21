package com.example.rambackend.entities;

import com.example.rambackend.enums.EtatNotification;
import com.example.rambackend.enums.NotificationFrom;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Notification {

    @Id
    private String id;
    private Utilisateur from;
    private LocalDateTime dateTime= LocalDateTime.now();
    private String desciption;


}
