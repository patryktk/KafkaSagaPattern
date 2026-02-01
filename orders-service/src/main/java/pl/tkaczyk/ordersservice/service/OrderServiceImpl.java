package pl.tkaczyk.ordersservice.service;


import org.springframework.stereotype.Service;
import pl.tkaczyk.core.dto.Order;
import pl.tkaczyk.core.types.OrderStatus;
import pl.tkaczyk.ordersservice.jpa.entity.OrderEntity;
import pl.tkaczyk.ordersservice.jpa.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order placeOrder(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setCustomerId(order.getCustomerId());
        entity.setProductId(order.getProductId());
        entity.setProductQuantity(order.getProductQuantity());
        entity.setStatus(OrderStatus.CREATED);
        orderRepository.save(entity);

        return new Order(
                entity.getId(),
                entity.getCustomerId(),
                entity.getProductId(),
                entity.getProductQuantity(),
                entity.getStatus());
    }

}
