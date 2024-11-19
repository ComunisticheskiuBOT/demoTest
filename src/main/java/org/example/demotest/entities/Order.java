package org.example.demotest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

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
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project project;

    @Column(name = "date_of_order")
    private Date dateOfOrder;
    @Column(name = "date_of_execution")
    private Date dateOfExecution;
    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(name = "order_description")
    // А нам может не хватить 255 символов для описания, такие поля либо расширяй, либо валидируй на входе
    private String orderDescription;
}
