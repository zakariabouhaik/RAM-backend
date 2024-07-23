package com.example.rambackend.servicesImpl;


import com.example.rambackend.entities.Regle;
import com.example.rambackend.entities.Section;
import com.example.rambackend.repository.SectionRepository;
import com.example.rambackend.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    @Override
    public Section saveSection(Section section) {

        

        return sectionRepository.save(section);
    }

    @Override
    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    @Override
    public Section getSectionById(String id) {
        return sectionRepository.findById(id).get();
    }

    @Override
    public Section updateSection(String id, Section section) {
        Section currSection = getSectionById(id);
        currSection.setDescription(section.getDescription());
        currSection.setRegles(section.getRegles());
        return sectionRepository.save(currSection);
    }

    @Override
    public void deleteSectionById(String id) {
        if (sectionRepository.existsById(id)) {
            sectionRepository.deleteById(id);
        }

    }
}