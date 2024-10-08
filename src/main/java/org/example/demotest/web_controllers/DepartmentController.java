package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestDepartment;
import org.example.demotest.entities.Department;
import org.example.demotest.services.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping("/department-api/v1/departments")
    public List<Department> findAllDepartments(){
        return departmentService.findAllDepartments();
    }

    @GetMapping("/department-api/v1/departments/{id}")
    public Department getDepartmentById(@PathVariable Long id) {
        return departmentService.findDepartmentById(id);
    }

    @GetMapping("/department-api/v1/departments/name/{name}")
    public ResponseEntity<Department> getDepartmentByDepartmentName(@PathVariable String name) {
        return departmentService.findDepartmentByDepartmentName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/department-api/v1/departments")
    public ResponseEntity<Department> processRequest(@RequestBody ServiceRequestDepartment serviceRequestDepartment) {
        return new ResponseEntity<>(departmentService.createDepartment(serviceRequestDepartment), HttpStatus.CREATED);
    }

    @DeleteMapping("/department-api/v1/departments/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/department-api/v1/departments/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody ServiceRequestDepartment updatedDepartment) {
        return departmentService.updatedClient(id, updatedDepartment)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
