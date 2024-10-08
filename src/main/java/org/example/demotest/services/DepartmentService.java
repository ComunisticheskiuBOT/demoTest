package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestDepartment;
import org.example.demotest.entities.Department;
import org.example.demotest.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Department> findAllDepartments(){
        return departmentRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Department createDepartment(ServiceRequestDepartment serviceRequestDepartment) {
        try {
            return departmentRepository.save(Department.builder()
                    .departmentName(serviceRequestDepartment.getDepartmentName())
                    .location(serviceRequestDepartment.getLocation())
                    .description(serviceRequestDepartment.getDescription())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании клиента", e);
        }
    }

    @Transactional(readOnly = true)
    public Department findDepartmentById(Long department_id) {
        return departmentRepository.findDepartmentByDepartmentId(department_id);
    }


    @Transactional(readOnly = true)
    public Optional<Department> findDepartmentByDepartmentName(String departmentName) {
        return departmentRepository.findByDepartmentName(departmentName);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public Optional<Department> updatedClient(Long id, ServiceRequestDepartment updatedDepartment) {
        return departmentRepository.findById(id).map(department -> {
            if (updatedDepartment.getDepartmentName() != null) {
                department.setDepartmentName(updatedDepartment.getDepartmentName());
            }
            if (updatedDepartment.getLocation() != null) {
                department.setLocation(updatedDepartment.getLocation());
            }
            if (updatedDepartment.getDescription() != null) {
                department.setDescription(updatedDepartment.getDescription());
            }

            return departmentRepository.save(department);
        });
    }
}
