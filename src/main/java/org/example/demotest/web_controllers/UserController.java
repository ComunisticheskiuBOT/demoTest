package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequest;
import org.example.demotest.entities.Role;
import org.example.demotest.entities.User;
import org.example.demotest.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user-api/v1/users")
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }

    @GetMapping("/user-api/v1/additional-info")
    public List<User> findAllUsersNotFromCountry(@RequestParam Role role){ return userService.findAllUsersNotFromCountry(role); }

    @PostMapping("/user-api/v1/users")
    public ResponseEntity<User> processRequest(@RequestBody ServiceRequest serviceRequest) {
        return new ResponseEntity<>(userService.createUser(serviceRequest), HttpStatus.CREATED);
    }
}
