package com.example.rambackend.servicesImpl;


import com.example.rambackend.entities.Regle;
import com.example.rambackend.entities.Section;
import com.example.rambackend.repository.RegleRepository;
import com.example.rambackend.repository.SectionRepository;
import com.example.rambackend.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionRepository sectionRepository;
    @Autowired
    private RegleRepository regleRepository;

    @Override
    public Section saveSection(Section section) {

        List<Regle> existingRegles = regleRepository.findAllById(
                section.getRegles().stream().map(Regle::getId).collect(Collectors.toList())
        );


        section.setRegles(existingRegles);

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