package pl.tkaczyk.ordersservice.service;



import pl.tkaczyk.core.types.OrderStatus;
import pl.tkaczyk.ordersservice.dto.OrderHistory;

import java.util.List;
import java.util.UUID;

public interface OrderHistoryService {
    void add(UUID orderId, OrderStatus orderStatus);

    List<OrderHistory> findByOrderId(UUID orderId);
}
