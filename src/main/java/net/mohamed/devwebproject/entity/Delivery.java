package net.mohamed.devwebproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mohamed.devwebproject.enums.DeliveryStatus;

import java.util.Date;

@Entity
@Table(name = "deliveries")
@Data
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date estimatedDate;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String deliveryAddress;
    private String trackingNumber;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}