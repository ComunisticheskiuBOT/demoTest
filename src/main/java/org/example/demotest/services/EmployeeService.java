package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestEmployee;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.Employee;
import org.example.demotest.repository.DepartmentRepository;
import org.example.demotest.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Employee> findAllEmployees(){
        return employeeRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Employee createEmployee(ServiceRequestEmployee serviceRequestEmployee) {
        try {
            departmentRepository.findById(serviceRequestEmployee.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Департамент с ID '" + serviceRequestEmployee.getDepartmentId() + "' не найден"));

            return employeeRepository.save(Employee.builder()
                    .firstName(serviceRequestEmployee.getFirstName())
                    .secondName(serviceRequestEmployee.getSecondName())
                    .lastName(serviceRequestEmployee.getLastName())
                    .birthDate(serviceRequestEmployee.getBirthDate())
                    .passportNumber(serviceRequestEmployee.getPassportNumber())
                    .address(serviceRequestEmployee.getAddress())
                    .phoneNumber(serviceRequestEmployee.getPhoneNumber())
                    .email(serviceRequestEmployee.getEmail())
                    .passWord(serviceRequestEmployee.getPassword())
                    .hireDate(serviceRequestEmployee.getHireDate())
                    .terminationDate(serviceRequestEmployee.getTerminationDate())
                    .departmentId(serviceRequestEmployee.getDepartmentId())
                    .position(serviceRequestEmployee.getPosition())
                    .status(serviceRequestEmployee.getStatus())
                    .salary(serviceRequestEmployee.getSalary())
                    .comments(serviceRequestEmployee.getComments())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании сотрудника", e);
        }
    }

    @Transactional(readOnly = true)
    public Employee findEmployeeById(Long id) {
        return employeeRepository.findEmployeeById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findEmployeeByPassportNumber(Integer passportNumber) {
        return employeeRepository.findByPassportNumber(passportNumber);
    }

    @Transactional(readOnly = true)
    public Optional<Employee> findEmployeeByPhoneNumber(String phoneNumber) {
        return employeeRepository.findByPhoneNumber(phoneNumber);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public boolean validateUser(Long passportNumber, String password) {
        return employeeRepository.findByPassportNumberAndPassWord(passportNumber, password) != null;
    }

    public boolean departmentExists(Long departmentId) {
        return departmentRepository.findById(departmentId).isPresent();
    }

    public Optional<Employee> updateEmployee(Long id, ServiceRequestEmployee updatedEmployee) {
        return employeeRepository.findById(id).map(employee -> {
            if (updatedEmployee.getFirstName() != null) {
                employee.setFirstName(updatedEmployee.getFirstName());
            }
            if (updatedEmployee.getSecondName() != null) {
                employee.setSecondName(updatedEmployee.getSecondName());
            }
            if (updatedEmployee.getLastName() != null) {
                employee.setLastName(updatedEmployee.getLastName());
            }
            if (updatedEmployee.getBirthDate() != null) {
                employee.setBirthDate(updatedEmployee.getBirthDate());
            }
            if (updatedEmployee.getPassportNumber() != null) {
                employee.setPassportNumber(updatedEmployee.getPassportNumber());
            }
            if (updatedEmployee.getAddress() != null) {
                employee.setAddress(updatedEmployee.getAddress());
            }
            if (updatedEmployee.getPhoneNumber() != null) {
                employee.setPhoneNumber(updatedEmployee.getPhoneNumber());
            }
            if (updatedEmployee.getEmail() != null) {
                employee.setEmail(updatedEmployee.getEmail());
            }
            if (updatedEmployee.getPassword() != null) {
                employee.setPassWord(updatedEmployee.getPassword());
            }
            if (updatedEmployee.getHireDate() != null) {
                employee.setHireDate(updatedEmployee.getHireDate());
            }
            if (updatedEmployee.getTerminationDate() != null) {
                employee.setTerminationDate(updatedEmployee.getTerminationDate());
            }
            if (updatedEmployee.getPosition() != null) {
                employee.setPosition(updatedEmployee.getPosition());
            }
            if (updatedEmployee.getStatus() != null) {
                employee.setStatus(updatedEmployee.getStatus());
            }
            if (updatedEmployee.getSalary() != null) {
                employee.setSalary(updatedEmployee.getSalary());
            }
            if (updatedEmployee.getComments() != null) {
                employee.setComments(updatedEmployee.getComments());
            }

            return employeeRepository.save(employee);
        });
    }
}
