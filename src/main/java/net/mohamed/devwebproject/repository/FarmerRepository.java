package net.mohamed.devwebproject.repository;

import net.mohamed.devwebproject.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer,Long> {
}
