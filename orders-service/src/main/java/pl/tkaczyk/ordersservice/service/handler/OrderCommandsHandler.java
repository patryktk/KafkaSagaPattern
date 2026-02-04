package pl.tkaczyk.ordersservice.service.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.tkaczyk.core.dto.commands.ApproveOrderCommand;
import pl.tkaczyk.core.dto.commands.RejectOrderCommand;
import pl.tkaczyk.ordersservice.service.OrderService;

@Component
@KafkaListener(topics = "${orders.commands.topic.name}")
@RequiredArgsConstructor
public class OrderCommandsHandler {

    private final OrderService orderService;

    @KafkaHandler
    public void handleCommand(@Payload ApproveOrderCommand approveOrderCommand) {
        orderService.approveOrder(approveOrderCommand.getOrderId());
    }

    @KafkaHandler
    public void handleCommand(@Payload RejectOrderCommand rejectOrderCommand) {
        orderService.rejectOrder(rejectOrderCommand.getOrderId());
    }
}
