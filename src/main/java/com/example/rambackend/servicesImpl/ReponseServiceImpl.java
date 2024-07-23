package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Reponse;
import com.example.rambackend.repository.ReponseRepository;
import com.example.rambackend.services.ReponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ReponseServiceImpl implements ReponseService {
    @Autowired
    private ReponseRepository reponseRepository;
    @Override
    public Reponse saveReponse(Reponse reponse) {
        return reponseRepository.save(reponse);
    }

    @Override
    public List<Reponse> getAllReponses() {
        return reponseRepository.findAll();
    }

    @Override
    public Reponse getReponseById(String id) {
        return reponseRepository.findById(id).get();
    }

    @Override
    public Reponse updateReponse(String id, Reponse reponse) {
        Reponse currReponse = getReponseById(id);
        currReponse.setReps(reponse.isReps());
        return reponseRepository.save(currReponse);
    }

    @Override
    public void deleteReponseById(String id) {
        if (reponseRepository.existsById(id)) {
            reponseRepository.deleteById(id);
        }
    }
}
