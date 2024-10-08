package org.example.demotest.services;

import lombok.RequiredArgsConstructor;
import org.example.demotest.dto.ServiceRequestOrder;
import org.example.demotest.entities.Client;
import org.example.demotest.entities.Department;
import org.example.demotest.entities.Order;
import org.example.demotest.entities.Project;
import org.example.demotest.repository.ClientRepository;
import org.example.demotest.repository.OrderRepository;
import org.example.demotest.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;

    @Transactional(readOnly = true)
    public List<Order> findAllOrders(){
        return orderRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Order createOrder(ServiceRequestOrder serviceRequestOrder) {
        try {
            clientRepository.findById(serviceRequestOrder.getClient().getClientId())
                    .orElseThrow(() -> new RuntimeException("Департамент с ID '" + serviceRequestOrder.getClient().getClientId() + "' не найден"));

            return orderRepository.save(Order.builder()
                    .orderId(serviceRequestOrder.getOrderId())
                    .client(serviceRequestOrder.getClient())
                    .project(serviceRequestOrder.getProject())
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

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Проект с ID '" + projectId + "' не найден"));
    }

    @Transactional(readOnly = true)
    public Client getClientById(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Клиент с ID '" + clientId + "' не найден"));
    }

    public boolean clientExists(Long clientId) {
        return clientRepository.findById(clientId).isPresent();
    }
    public boolean projectExists(Long projectId) {
        return projectRepository.findById(projectId).isPresent();
    }

    public Optional<Order> updateOrder(Long id, ServiceRequestOrder updatedOrder) {
        return orderRepository.findById(id).map(order -> {
            if (updatedOrder.getOrderId() != null) {
                order.setOrderId(updatedOrder.getOrderId());
            }
            if (updatedOrder.getClient() != null) {
                order.setClient(updatedOrder.getClient());
            }
            if (updatedOrder.getProject() != null) {
                order.setProject(updatedOrder.getProject());
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
