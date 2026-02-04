package pl.tkaczyk.productservice.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.tkaczyk.core.dto.Product;
import pl.tkaczyk.core.dto.commands.CancelProductReservationCommand;
import pl.tkaczyk.core.dto.commands.ProductReservationCancelledEvent;
import pl.tkaczyk.core.dto.commands.ReserveProductCommand;
import pl.tkaczyk.core.dto.events.ProductReservationFailedEvent;
import pl.tkaczyk.core.dto.events.ProductReservedEvent;
import pl.tkaczyk.productservice.service.ProductService;

@KafkaListener(topics = "${products.commands.topic.name}")
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductCommandsHandler {

    private final ProductService productService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment environment;

    @KafkaHandler
    public void handleCommand(@Payload ReserveProductCommand command) {

        try {
            Product desiredProduct = new Product(command.getProductId(), command.getProductQuantity());
            Product reservedProduct = productService.reserve(desiredProduct, command.getOrderId());

            ProductReservedEvent productReservedEvent = new ProductReservedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getProductQuantity(),
                    reservedProduct.getPrice());

            kafkaTemplate.send(environment.getProperty("products.events.topic.name"), productReservedEvent);

        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
            ProductReservationFailedEvent productReservationFailedEvent = new ProductReservationFailedEvent(
                    command.getProductId(),
                    command.getProductId(),
                    command.getProductQuantity()
            );
            kafkaTemplate.send(environment.getProperty("products.events.topic.name"), productReservationFailedEvent);
        }
    }

    @KafkaHandler
    public void handleEvent(@Payload CancelProductReservationCommand command) {
        Product productToCancel = new Product(command.getProductId(), command.getProductQuantity());
        productService.cancelReservation(productToCancel, command.getOrderId());

        ProductReservationCancelledEvent productReservationCancelledEvent = new ProductReservationCancelledEvent(command.getProductId(), command.getProductId());
        kafkaTemplate.send(environment.getProperty("products.events.topic.name"), productReservationCancelledEvent);
    }
}
