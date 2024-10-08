package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestClient;
import org.example.demotest.entities.Client;
import org.example.demotest.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientService {
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<Client> findAllClients(){
        return clientRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Client createClient(ServiceRequestClient serviceRequestClient) {
        try {
            return clientRepository.save(Client.builder()
                    .companyName(serviceRequestClient.getCompanyName())
                    .contactPerson(serviceRequestClient.getContactPerson())
                    .phoneNumber(serviceRequestClient.getPhoneNumber())
                    .email(serviceRequestClient.getEmail())
                    .address(serviceRequestClient.getAddress())
                    .reputation(serviceRequestClient.getReputation())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании клиента", e);
        }
    }

    @Transactional(readOnly = true)
    public Client findClientById(Long client_id) {
        return clientRepository.findClientByClientId(client_id);
    }


    @Transactional(readOnly = true)
    public Optional<Client> findClientByPhoneNumber(String phoneNumber) {
        return clientRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public Optional<Client> updatedClient(Long id, ServiceRequestClient updatedClient) {
        return clientRepository.findById(id).map(client -> {
            if (updatedClient.getCompanyName() != null) {
                client.setCompanyName(updatedClient.getCompanyName());
            }
            if (updatedClient.getContactPerson() != null) {
                client.setContactPerson(updatedClient.getContactPerson());
            }
            if (updatedClient.getPhoneNumber() != null) {
                client.setPhoneNumber(updatedClient.getPhoneNumber());
            }
            if (updatedClient.getEmail() != null) {
                client.setEmail(updatedClient.getEmail());
            }
            if (updatedClient.getAddress() != null) {
                client.setAddress(updatedClient.getAddress());
            }
            if (updatedClient.getReputation() != null) {
                client.setReputation(updatedClient.getReputation());
            }

            return clientRepository.save(client);
        });
    }
}
