package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestProject;
import org.example.demotest.entities.Project;
import org.example.demotest.repository.EmployeeRepository;
import org.example.demotest.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<Project> findAllProjects(){
        return projectRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Project createProject(ServiceRequestProject serviceRequestProject) {
        try {
            // Прочитай про modelMapper
            return projectRepository.save(Project.builder()
                    .engineerId(serviceRequestProject.getEngineerId())
                    .projectName(serviceRequestProject.getProjectName())
                    .drawing(serviceRequestProject.getDrawing())
                    .materialType(serviceRequestProject.getMaterialType())
                    .expectedTime(serviceRequestProject.getExpectedTime())
                    .projectDescription(serviceRequestProject.getProjectDescription())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании проекта", e);
        }
    }

    @Transactional(readOnly = true)
    public Project findProjectById(Long project_id) {
        return projectRepository.findProjectByProjectId(project_id);
    }


    @Transactional(readOnly = true)
    public Optional<Project> findByProjectName(String projectName) {
        return projectRepository.findByProjectName(projectName);
    }

    @Transactional(readOnly = true)
    public Optional<Project> findProjectByDrawing(String drawing) {
        return projectRepository.findByDrawing(drawing);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public boolean engineerExists(Long engineerId) {
        return employeeRepository.findById(engineerId).isPresent();
    }

    public Optional<Project> updatedProject(Long id, ServiceRequestProject updatedProject) {
        // Прочитай про modelMapper
        return projectRepository.findById(id).map(project -> {
            if (updatedProject.getEngineerId() != null) {
                project.setEngineerId(updatedProject.getEngineerId());
            }
            if (updatedProject.getProjectName() != null) {
                project.setProjectName(updatedProject.getProjectName());
            }
            if (updatedProject.getDrawing() != null) {
                project.setDrawing(updatedProject.getDrawing());
            }
            if (updatedProject.getMaterialType() != null) {
                project.setMaterialType(updatedProject.getMaterialType());
            }
            if (updatedProject.getExpectedTime() != null) {
                project.setExpectedTime(updatedProject.getExpectedTime());
            }
            if (updatedProject.getProjectDescription() != null) {
                project.setProjectDescription(updatedProject.getProjectDescription());
            }
            return projectRepository.save(project);
        });
    }
}
