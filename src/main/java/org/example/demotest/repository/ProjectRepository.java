package org.example.demotest.repository;

import org.example.demotest.entities.Department;
import org.example.demotest.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findProjectByProjectId(Long project_id);
    Optional<Project> findByProjectName(String projectName);
    Optional<Project> findByDrawing(String drawing);
}
