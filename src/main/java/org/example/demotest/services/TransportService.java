package org.example.demotest.services;

import org.example.demotest.entities.Transport;
import org.example.demotest.repository.TransportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportService {
    @Autowired
    private TransportRepository transportRepository;
    public List<Transport> findAll(){ return transportRepository.findAll(); }

    public Transport addTransport(Transport transport){
        return transportRepository.save(transport);
    }
}
