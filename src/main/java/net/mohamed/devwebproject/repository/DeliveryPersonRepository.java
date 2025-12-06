package net.mohamed.devwebproject.repository;

import net.mohamed.devwebproject.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    List<DeliveryPerson> findByAvailable(Boolean available);
}