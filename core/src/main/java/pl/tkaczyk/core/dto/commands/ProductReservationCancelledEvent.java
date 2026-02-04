package pl.tkaczyk.core.dto.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductReservationCancelledEvent {
    private UUID productId;
    private UUID orderId;
}
