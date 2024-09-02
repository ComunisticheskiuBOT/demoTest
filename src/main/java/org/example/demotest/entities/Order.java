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
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long order_id;
    @Column(name = "customer_id")
    private Long customer_id;
    @Column(name = "product_id")
    private Long product_id;
    @Column(name = "name_of_order")
    private String name_of_order;
    @Column(name = "name_of_customer")
    private String name_of_customer;
    @Column(name = "date_of_delivery")
    private String date_of_delivery;
    @Column(name = "date_of_acceptance")
    private String date_of_acceptance;
    @Column(name = "order_description")
    private String order_description;
}
