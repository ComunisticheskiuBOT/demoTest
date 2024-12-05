package org.example.demotest.repository;

import org.example.demotest.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findEmployeeById(Long id);
    Optional<Employee> findByPassportNumber(Long passportNumber);
    Optional<Employee> findByInn(Long inn);
    Optional<Employee> findBySnils(Long snils);
    Employee findByPassportNumberAndPassWord(Long passportNumber, String password);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
}
