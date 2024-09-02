package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequest;
import org.example.demotest.entities.Role;
import org.example.demotest.entities.User;
import org.example.demotest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    @Transactional(readOnly = true)

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsersNotFromCountry(Role role){ return userRepository.findAllUsersNotFromCountry(role); }

    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(ServiceRequest serviceRequest) {
        return userRepository.save(User.builder()
                .first_name(serviceRequest.getFirst_name())
                .second_name(serviceRequest.getSecond_name())
                .last_name(serviceRequest.getLast_name())
                .role(serviceRequest.getRole())
                .user_description(serviceRequest.getUser_description())
                .user_password(serviceRequest.getUser_password())
                .build());
    }}
