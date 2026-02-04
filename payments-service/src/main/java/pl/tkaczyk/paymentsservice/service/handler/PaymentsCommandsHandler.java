package pl.tkaczyk.paymentsservice.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.tkaczyk.core.dto.commands.ProcessedPaymentCommand;
import pl.tkaczyk.core.dto.Payment;
import pl.tkaczyk.core.dto.events.PaymentFailedEvent;
import pl.tkaczyk.core.dto.events.PaymentProcessedEvent;
import pl.tkaczyk.core.exceptions.CreditCardProcessorUnavailableException;
import pl.tkaczyk.paymentsservice.service.PaymentService;

@RequiredArgsConstructor
@Component
@KafkaListener(topics = "${payments.commands.topic.name}")
@Slf4j
public class PaymentsCommandsHandler {

    private final PaymentService paymentService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Environment environment;

    @KafkaHandler
    public void handleCommand(@Payload ProcessedPaymentCommand command) {
        try {
            Payment payment = new Payment(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getProductPrice(),
                    command.getProductQuantity());

            Payment processedPayment = paymentService.process(payment);
            PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
                    processedPayment.getOrderId(),
                    processedPayment.getId());
            kafkaTemplate.send(environment.getProperty("payments.events.topic.name"), paymentProcessedEvent);

        } catch (CreditCardProcessorUnavailableException e) {
            log.error(e.getLocalizedMessage(), e);
            PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                    command.getOrderId(),
                    command.getProductId(),
                    command.getProductQuantity());

            kafkaTemplate.send(environment.getProperty("payments.events.topic.name"), paymentFailedEvent);
        }
    }
}
