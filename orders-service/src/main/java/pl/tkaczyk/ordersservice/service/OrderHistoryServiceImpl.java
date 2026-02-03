package pl.tkaczyk.ordersservice.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import pl.tkaczyk.core.types.OrderStatus;
import pl.tkaczyk.ordersservice.dto.OrderHistory;
import pl.tkaczyk.ordersservice.jpa.entity.OrderHistoryEntity;
import pl.tkaczyk.ordersservice.jpa.repository.OrderHistoryRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryServiceImpl(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public void add(UUID orderId, OrderStatus orderStatus) {
        OrderHistoryEntity entity = new OrderHistoryEntity();
        entity.setOrderId(orderId);
        entity.setStatus(orderStatus);
        entity.setCreatedAt(new Timestamp(new Date().getTime()));
        orderHistoryRepository.save(entity);
    }

    @Override
    public List<OrderHistory> findByOrderId(UUID orderId) {
        var entities = orderHistoryRepository.findByOrderId(orderId);
        return entities.stream().map(entity -> {
            OrderHistory orderHistory = new OrderHistory();
            BeanUtils.copyProperties(entity, orderHistory);
            return orderHistory;
        }).collect(Collectors.toList());
    }
}
