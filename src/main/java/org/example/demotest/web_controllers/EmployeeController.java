package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestEmployee;
import org.example.demotest.entities.Employee;
import org.example.demotest.entities.Status;
import org.example.demotest.services.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/employee-api/v1/employees")
    public List<Employee> findAllEmployees(){
        return employeeService.findAllEmployees();
    }

    @GetMapping("/employee-api/v1/employees/{id}")
    public Employee getEmployeeById(@PathVariable Long id) {
        return employeeService.findEmployeeById(id);
    }

    @GetMapping("/employee-api/v1/employees/passport/{passportNumber}")
    public ResponseEntity<Employee> getEmployeeByPassportNumber(@PathVariable Integer passportNumber) {
        return employeeService.findEmployeeByPassportNumber(passportNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee-api/v1/employees/phone/{phoneNumber}")
    public ResponseEntity<Employee> getEmployeeByPhoneNumber(@PathVariable String phoneNumber) {
        return employeeService.findEmployeeByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/employee-api/v1/employees")
    public ResponseEntity<Employee> processRequest(@RequestBody ServiceRequestEmployee serviceRequestEmployee) {
        return new ResponseEntity<>(employeeService.createEmployee(serviceRequestEmployee), HttpStatus.CREATED);
    }

    @DeleteMapping("/employee-api/v1/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/employee-api/v1/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody ServiceRequestEmployee updatedEmployee) {
        return employeeService.updateEmployee(id, updatedEmployee)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
