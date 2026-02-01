package pl.tkaczyk.ordersservice.service;


import pl.tkaczyk.core.dto.Order;

public interface OrderService {
    Order placeOrder(Order order);
}
