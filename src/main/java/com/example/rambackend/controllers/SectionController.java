package com.example.rambackend.controllers;


import com.example.rambackend.entities.Section;
import com.example.rambackend.services.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Section")
public class SectionController {
    @Autowired
    private SectionService sectionService;

    @PostMapping
    private Section addSection(@RequestBody Section section){
        return sectionService.saveSection(section);
    }
    @GetMapping("/{id}")
    private Section getSection(@PathVariable String id) {
        return sectionService.getSectionById(id);
    }

    @GetMapping()
    private List<Section> getAllSections() {
        return sectionService.getAllSections();
    }

    @PutMapping("/{id}")
    public Section editSection(@PathVariable String id, @RequestBody Section section) {
        return sectionService.updateSection(id, section);
    }

    @DeleteMapping("/{id}")
    public void deleteSection(@PathVariable String id) {
        sectionService.deleteSectionById(id);
    }

}