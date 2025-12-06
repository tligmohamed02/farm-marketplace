package net.mohamed.devwebproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_persons")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DeliveryPerson extends User {
    private String phone;
    private String vehicleType;
    private String vehicleNumber;
    private Boolean available = true;

    @OneToMany(mappedBy = "deliveryPerson")
    private List<Delivery> deliveries = new ArrayList<>();
}