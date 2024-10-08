package org.example.demotest.repository;

import org.example.demotest.entities.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findDepartmentByDepartmentId(Long department_id);
    Optional<Department> findByDepartmentName(String departmentName);
}
