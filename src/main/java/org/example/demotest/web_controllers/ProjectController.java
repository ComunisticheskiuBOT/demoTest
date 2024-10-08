package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestProject;
import org.example.demotest.entities.Project;
import org.example.demotest.services.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/project-api/v1/projects")
    public List<Project> findAllProjects(){
        return projectService.findAllProjects();
    }

    @GetMapping("/project-api/v1/projects/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return projectService.findProjectById(id);
    }

    @GetMapping("/project-api/v1/clients/projects/{projectName}")
    public ResponseEntity<Project> getProjectByProjectName(@PathVariable String projectName) {
        return projectService.findByProjectName(projectName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project-api/v1/clients/projects/{drawing}")
    public ResponseEntity<Project> getProjectByDrawing(@PathVariable String drawing) {
        return projectService.findProjectByDrawing(drawing)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/project-api/v1/projects")
    public ResponseEntity<Project> processRequest(@RequestBody ServiceRequestProject serviceRequestProject) {
        return new ResponseEntity<>(projectService.createProject(serviceRequestProject), HttpStatus.CREATED);
    }

    @DeleteMapping("/project-api/v1/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/project-api/v1/projects/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody ServiceRequestProject updatedProject) {
        return projectService.updatedProject(id, updatedProject)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
