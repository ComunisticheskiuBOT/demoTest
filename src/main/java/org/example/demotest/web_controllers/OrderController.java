package org.example.demotest.web_controllers;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestOrder;
import org.example.demotest.entities.Order;
import org.example.demotest.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/order-api/v1/orders")
    public List<Order> findAllOrders(){
        return orderService.findAllOrders();
    }

    @GetMapping("/order-api/v1/orders/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.findOrderById(id);
    }

    @PostMapping("/order-api/v1/orders")
    public ResponseEntity<Order> processRequest(@RequestBody ServiceRequestOrder serviceRequestOrder) {
        return new ResponseEntity<>(orderService.createOrder(serviceRequestOrder), HttpStatus.CREATED);
    }

    @DeleteMapping("/order-api/v1/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/order-api/v1/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody ServiceRequestOrder updatedOrder) {
        return orderService.updateOrder(id, updatedOrder)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
