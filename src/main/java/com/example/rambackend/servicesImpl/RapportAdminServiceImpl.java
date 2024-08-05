package com.example.rambackend.servicesImpl;

import com.example.rambackend.entities.RapportAdmin;
import com.example.rambackend.repository.NotificationRepository;
import com.example.rambackend.repository.RapportAdminRepository;
import com.example.rambackend.services.RapportAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RapportAdminServiceImpl implements RapportAdminService {

    @Autowired
    private RapportAdminRepository rapportAdminRepository;

    @Override
    public RapportAdmin saverapportAdmin(RapportAdmin rapportAdmin) {
        return rapportAdminRepository.save(rapportAdmin);
    }

    @Override
    public List<RapportAdmin> getAllrapportAdmins() {
        return rapportAdminRepository.findAll();
    }

    @Override
    public RapportAdmin getrapportAdminById(String id) {
        return rapportAdminRepository.findById(id).orElse(null);
    }

    @Override
    public void deleterapportAdminById(String id) {
        rapportAdminRepository.deleteById(id);
    }


}
