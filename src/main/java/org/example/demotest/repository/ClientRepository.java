package org.example.demotest.repository;

import org.example.demotest.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Client findClientByClientId(Long client_id);
    Optional<Client> findByPhoneNumber(String phoneNumber);
}
