package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.entities.Customer;
import org.example.demotest.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<Customer> findAll() { return customerRepository.findAll(); }
    @Transactional(propagation = Propagation.SUPPORTS)
    public Customer addCustomer(Customer customer) { return customerRepository.save(customer); }
}
