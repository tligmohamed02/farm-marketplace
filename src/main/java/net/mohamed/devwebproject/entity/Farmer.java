package net.mohamed.devwebproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farmers")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Farmer extends User {
    private String location;
    private String farmName;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
}
