package com.example.rambackend.services;

import com.example.rambackend.entities.Regle;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface RegleService {
    Regle saveRegle(Regle regle);
    List<Regle> getAllRegles();
    Regle getRegleById(String id);
    Regle updateRegle(String id, Regle regle);
    void deleteRegleById(String id);
}
