package net.mohamed.devwebproject.service;

import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Customer;
import net.mohamed.devwebproject.entity.Delivery;
import net.mohamed.devwebproject.entity.DeliveryPerson;
import net.mohamed.devwebproject.entity.Order;
import net.mohamed.devwebproject.enums.DeliveryStatus;
import net.mohamed.devwebproject.enums.OrderStatus;
import net.mohamed.devwebproject.repository.CustomerRepository;
import net.mohamed.devwebproject.repository.DeliveryPersonRepository;
import net.mohamed.devwebproject.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public Delivery createDelivery(Order order, Long deliveryPersonId) {
        Customer customer = order.getCustomer();

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryAddress(customer.getShippingAddress());
        delivery.setStatus(DeliveryStatus.PENDING);

        // Date estimée : 3 jours
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 3);
        delivery.setEstimatedDate(cal.getTime());

        // Générer tracking number
        delivery.setTrackingNumber("TRK" + System.currentTimeMillis());

        // Assigner livreur si fourni
        if (deliveryPersonId != null) {
            DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(deliveryPersonId)
                    .orElseThrow(() -> new RuntimeException("Livreur non trouvé"));
            delivery.setDeliveryPerson(deliveryPerson);
        }

        return deliveryRepository.save(delivery);
    }

    public List<DeliveryPerson> getAvailableDeliveryPersons() {
        return deliveryPersonRepository.findByAvailable(true);
    }

    public Delivery getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderOrderId(orderId).orElse(null);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @Transactional
    public Delivery updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Livraison non trouvée"));
        delivery.setStatus(status);

        if (status == DeliveryStatus.DELIVERED) {
            delivery.setDeliveryDate(new Date());
            // Mettre à jour le statut de la commande
            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.DELIVERED);
        }

        return deliveryRepository.save(delivery);
    }
}