package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestOrder;
import org.example.demotest.entities.Order;
import org.example.demotest.repository.ClientRepository;
import org.example.demotest.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    @Transactional(readOnly = true)
    public List<Order> findAllOrders(){
        return orderRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Order createOrder(ServiceRequestOrder serviceRequestOrder) {
        try {
            clientRepository.findById(serviceRequestOrder.getClientId())
                    .orElseThrow(() -> new RuntimeException("Департамент с ID '" + serviceRequestOrder.getClientId() + "' не найден"));

            return orderRepository.save(Order.builder()
                    .orderId(serviceRequestOrder.getOrderId())
                    .clientId(serviceRequestOrder.getClientId())
                    .projectId(serviceRequestOrder.getProjectId())
                    .dateOfExecution(serviceRequestOrder.getDateOfExecution())
                    .dateOfOrder(serviceRequestOrder.getDateOfOrder())
                    .orderStatus(serviceRequestOrder.getOrderStatus())
                    .orderDescription(serviceRequestOrder.getOrderDescription())
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании сотрудника", e);
        }
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public Order findOrderById(Long id) {
        return orderRepository.findOrderByOrderId(id);
    }


    public boolean clientExists(Long clientId) {
        return clientRepository.findById(clientId).isPresent();
    }

    public Optional<Order> updateOrder(Long id, ServiceRequestOrder updatedOrder) {
        return orderRepository.findById(id).map(order -> {
            if (updatedOrder.getOrderId() != null) {
                order.setOrderId(updatedOrder.getOrderId());
            }
            if (updatedOrder.getClientId() != null) {
                order.setClientId(updatedOrder.getClientId());
            }
            if (updatedOrder.getProjectId() != null) {
                order.setProjectId(updatedOrder.getProjectId());
            }
            if (updatedOrder.getDateOfOrder() != null) {
                order.setDateOfOrder(updatedOrder.getDateOfOrder());
            }
            if (updatedOrder.getDateOfExecution() != null) {
                order.setDateOfExecution(updatedOrder.getDateOfExecution());
            }
            if (updatedOrder.getOrderStatus() != null) {
                order.setOrderStatus(updatedOrder.getOrderStatus());
            }
            if (updatedOrder.getOrderDescription() != null) {
                order.setOrderDescription(updatedOrder.getOrderDescription());
            }

            return orderRepository.save(order);
        });
    }
}
