package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.entities.Employee;
import org.example.demotest.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public List<Employee> findAll() { return employeeRepository.findAll(); }
    public Employee addEmployee(Employee employee) { return  employeeRepository.save(employee); }
}
