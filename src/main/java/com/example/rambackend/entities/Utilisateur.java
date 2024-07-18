package com.example.rambackend.entities;

import com.example.rambackend.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Utilisateur {
    @Id
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @GeneratedValue(strategy = GenerationType.AUTO,generator = "UUID")
    private UUID id;
    private String nom_complet;
    private String email;
    private String mdp;
    private String tel;
    private LocalDateTime DateCreation= LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
