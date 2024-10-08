package org.example.demotest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "Projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "engineer_id", referencedColumnName = "id", insertable=false, updatable=false)
    private Employee employee;

    @Column(name = "engineer_id")
    private Long engineerId;

    @Column(name = "project_name")
    private String projectName;
    @Column(name = "drawing")
    private String drawing;
    @Column(name = "material_type")
    private MaterialType materialType;
    @Column(name = "expected_time")
    private Duration expectedTime;
    @Column(name = "project_description")
    private String projectDescription;
}
