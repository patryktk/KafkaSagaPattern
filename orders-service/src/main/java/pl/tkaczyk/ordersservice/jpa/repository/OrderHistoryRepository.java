package pl.tkaczyk.ordersservice.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.tkaczyk.ordersservice.jpa.entity.OrderHistoryEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistoryEntity, UUID> {
    List<OrderHistoryEntity> findByOrderId(UUID orderId);
}
