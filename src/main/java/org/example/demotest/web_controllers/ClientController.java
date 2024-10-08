package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestClient;
import org.example.demotest.entities.Client;
import org.example.demotest.services.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @GetMapping("/client-api/v1/clients")
    public List<Client> findAllClients(){
        return clientService.findAllClients();
    }

    @GetMapping("/client-api/v1/clients/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientService.findClientById(id);
    }

    @GetMapping("/client-api/v1/clients/phone/{phoneNumber}")
    public ResponseEntity<Client> getClientByPhoneNumber(@PathVariable String phoneNumber) {
        return clientService.findClientByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/client-api/v1/clients")
    public ResponseEntity<Client> processRequest(@RequestBody ServiceRequestClient serviceRequestClient) {
        return new ResponseEntity<>(clientService.createClient(serviceRequestClient), HttpStatus.CREATED);
    }

    @DeleteMapping("/client-api/v1/clients/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/client-api/v1/clients/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody ServiceRequestClient updatedClient) {
        return clientService.updatedClient(id, updatedClient)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
