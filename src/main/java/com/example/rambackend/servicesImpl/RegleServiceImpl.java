package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.Regle;
import com.example.rambackend.repository.RegleRepository;
import com.example.rambackend.services.RegleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RegleServiceImpl implements RegleService {
    @Autowired
    private RegleRepository regleRepository;

    @Override
    public Regle saveRegle(Regle regle) {
        return regleRepository.save(regle);
    }

    @Override
    public List<Regle> getAllRegles() {
        return regleRepository.findAll();
    }

    @Override
    public Regle getRegleById(String id) {
        return regleRepository.findById(id).get();
    }

    @Override
    public Regle updateRegle(String id, Regle regle) {
        Regle currRegle = getRegleById(id);
        currRegle.setDescription(regle.getDescription());
        return regleRepository.save(currRegle);
    }

    @Override
    public void deleteRegleById(String id) {
        if (regleRepository.existsById(id)) {
            regleRepository.deleteById(id);
        }
    }

}
