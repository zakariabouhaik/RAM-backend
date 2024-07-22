package com.example.rambackend.entities;

import com.example.rambackend.enums.EtatNotification;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@Document
public class Notification {

    @Id
    private String id;
    private String desciption;
    private int priorite;
    private EtatNotification etatNotification;

}
