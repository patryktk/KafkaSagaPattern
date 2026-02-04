package pl.tkaczyk.ordersservice.service;


import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import pl.tkaczyk.core.dto.Order;
import pl.tkaczyk.core.dto.events.OrderApprovedEvent;
import pl.tkaczyk.core.dto.events.OrderCreatedEvent;
import pl.tkaczyk.core.types.OrderStatus;
import pl.tkaczyk.ordersservice.jpa.entity.OrderEntity;
import pl.tkaczyk.ordersservice.jpa.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment environment;

    @Override
    public Order placeOrder(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(order.getCustomerId());
        entity.setProductId(order.getProductId());
        entity.setProductQuantity(order.getProductQuantity());
        entity.setStatus(OrderStatus.CREATED);
        orderRepository.save(entity);

        OrderCreatedEvent placeOrder = new OrderCreatedEvent(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductQuantity()
        );
        kafkaTemplate.send(environment.getProperty("orders.events.topic.name"), placeOrder);

        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductQuantity(),
                entity.getStatus());
    }

    @Override
    public void approveOrder(UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        Assert.notNull(orderEntity, "Order not found with id " + orderId);
        orderEntity.setStatus(OrderStatus.APPROVED);
        orderRepository.save(orderEntity);
        OrderApprovedEvent orderApprovedEvent = new OrderApprovedEvent(orderId);
        kafkaTemplate.send(environment.getProperty("orders.events.topic.name"), orderApprovedEvent);
    }

    @Override
    public void rejectOrder(UUID orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        Assert.notNull(orderEntity, "Order not found with id " + orderId);
        orderEntity.setStatus(OrderStatus.REJECTED);
        orderRepository.save(orderEntity);
    }
}
