package org.example.demotest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "Materials")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "material_id")
    private Long material_id;
    @Column(name = "name_of_material")
    private String name_of_material;
    @Column(name = "price")
    private Long price;
    @Column(name = "material_description")
    private String material_description;
    @Column(name = "amount")
    private Long amount;
}
