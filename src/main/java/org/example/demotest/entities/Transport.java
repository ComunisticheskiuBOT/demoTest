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
@Table(name = "Transport")
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transport_id")
    private Long transport_id;
    @Column(name = "order_id")
    private Long order_id;
    @Column(name = "date_of_loading")
    private String date_of_loading;
    @Column(name = "date_of_unloading")
    private String date_of_unloading;
    @Column(name = "price")
    private Long price;
    @Column(name = "transport_description")
    private String transport_description;
}
