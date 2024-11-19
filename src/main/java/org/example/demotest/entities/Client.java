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
@Table(name = "\"clients\"") //Зачем внутри ковычки? Если совпадает с системным слово в БД, то лучше меняй название
public class Client {
    @Id
    @Column(name = "client_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    /*
    Генерацию айдишников можно вынести в один абстрактный класс с аннотаций @MappedSuperclass и наследовать все сущности от него
    Как будто не хватает мета информации - вроде createTs / updateTs
     */

    @Column(name = "company_name")
    private String companyName;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "enail")
    private String email;
    @Column(name = "address")
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(name = "reputation")
    private Reputation reputation;
}
