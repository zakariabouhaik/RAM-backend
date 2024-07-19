package com.example.rambackend.services;

import com.example.rambackend.entities.Reponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ReponseService {
    Reponse saveReponse(Reponse reponse);
    List<Reponse> getAllReponses();
    Reponse getReponseById(Long id);
    Reponse updateReponse(Long id, Reponse reponse);
    void deleteReponseById(Long id);
}
