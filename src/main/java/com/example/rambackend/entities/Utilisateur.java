package com.example.rambackend.entities;

import com.example.rambackend.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Utilisateur {
    @Id
    private String id;
    private String Fullname;
    private String email;
    private String mdp;
    private UserRole role;
    private Boolean Enabled;
    private String IdMongo;
    private Long createdTimestamp;
    private List<Notification>notifications;


}
