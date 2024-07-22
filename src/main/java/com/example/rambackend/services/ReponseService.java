package com.example.rambackend.services;

import com.example.rambackend.entities.Reponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ReponseService {
    Reponse saveReponse(Reponse reponse);
    List<Reponse> getAllReponses();
    Reponse getReponseById(String id);
    Reponse updateReponse(String id, Reponse reponse);
    void deleteReponseById(String id);
}
