package org.example.demotest.services;

import org.example.demotest.entities.Material;
import org.example.demotest.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> findAll() { return materialRepository.findAll(); }
    public Material addMaterial(Material material) { return  materialRepository.save(material); }
}
