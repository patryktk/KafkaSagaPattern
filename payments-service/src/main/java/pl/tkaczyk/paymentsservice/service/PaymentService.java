package pl.tkaczyk.paymentsservice.service;


import pl.tkaczyk.core.dto.Payment;

import java.util.List;

public interface PaymentService {
    List<Payment> findAll();

    Payment process(Payment payment);
}
