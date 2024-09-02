package org.example.demotest.services;

import org.example.demotest.entities.Order;
import org.example.demotest.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> findAll() { return orderRepository.findAll(); }
    public Order addOrder(Order order) { return orderRepository.save(order); }

}
