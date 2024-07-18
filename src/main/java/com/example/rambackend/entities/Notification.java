package com.example.rambackend.entities;

import com.example.rambackend.enums.EtatNotification;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String desciption;
    private int priorite;
    @Enumerated(EnumType.STRING)
    private EtatNotification etatNotification;

}
