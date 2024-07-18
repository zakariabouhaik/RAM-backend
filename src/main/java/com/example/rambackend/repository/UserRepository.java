package com.example.rambackend.repository;

import com.example.rambackend.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Utilisateur,UUID> {
}
