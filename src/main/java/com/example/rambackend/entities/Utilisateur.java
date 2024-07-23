package com.example.rambackend.entities;

import com.example.rambackend.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Utilisateur {
    @Id
    private String id;
    private String nom_complet;
    private String email;
    private String mdp;
    private String tel;
    private LocalDateTime DateCreation= LocalDateTime.now();
    private UserRole role;
}
