package net.mohamed.devwebproject.repository;

import net.mohamed.devwebproject.entity.Delivery;
import net.mohamed.devwebproject.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByStatus(DeliveryStatus status);
    Optional<Delivery> findByOrderOrderId(Long orderId);
    List<Delivery> findByDeliveryPersonUserId(Long deliveryPersonId);
}
