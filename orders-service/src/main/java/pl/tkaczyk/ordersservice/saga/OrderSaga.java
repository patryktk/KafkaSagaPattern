package pl.tkaczyk.ordersservice.saga;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.tkaczyk.core.dto.commands.*;
import pl.tkaczyk.core.dto.events.*;
import pl.tkaczyk.core.types.OrderStatus;
import pl.tkaczyk.ordersservice.service.OrderHistoryService;

@Component
@KafkaListener(topics = {
        "${orders.events.topic.name}",
        "${products.events.topic.name}",
        "${payments.events.topic.name}"
})
@RequiredArgsConstructor
public class OrderSaga {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment environment;
    private final OrderHistoryService orderHistoryService;

    @KafkaHandler
    public void handleEvent(@Payload OrderCreatedEvent event) {
        ReserveProductCommand command = new ReserveProductCommand(
                event.getProductId(),
                event.getProductQuantity(),
                event.getOrderId()
        );

        kafkaTemplate.send(environment.getProperty("products.commands.topic.name"), command);
        orderHistoryService.add(event.getOrderId(), OrderStatus.CREATED);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservedEvent event) {
        ProcessedPaymentCommand processedPaymentCommand = new ProcessedPaymentCommand(
                event.getOrderId(),
                event.getProductId(),
                event.getProductPrice(),
                event.getProductQuantity()
        );
        kafkaTemplate.send(environment.getProperty("payments.commands.topic.name"), processedPaymentCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentProcessedEvent event) {
        ApproveOrderCommand approveOrderCommand = new ApproveOrderCommand(event.getOrderId());
        kafkaTemplate.send(environment.getProperty("orders.commands.topic.name"), approveOrderCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload OrderApprovedEvent event) {
        orderHistoryService.add(event.getOrderId(), OrderStatus.APPROVED);
    }

    @KafkaHandler
    public void handleEvent(@Payload PaymentFailedEvent event) {
        CancelProductReservationCommand cancelProductReservationCommand = new CancelProductReservationCommand(
                event.getOrderId(),
                event.getProductId(),
                event.getProductQuantity());
        kafkaTemplate.send(environment.getProperty("products.commands.topic.name"), cancelProductReservationCommand);
    }

    @KafkaHandler
    public void handleEvent(@Payload ProductReservationCancelledEvent event) {
        RejectOrderCommand rejectOrderCommand = new RejectOrderCommand(event.getOrderId());
        kafkaTemplate.send(environment.getProperty("orders.commands.topic.name"), rejectOrderCommand);
        orderHistoryService.add(event.getOrderId(), OrderStatus.REJECTED);
    }
}
