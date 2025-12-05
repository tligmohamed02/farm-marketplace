package net.mohamed.devwebproject.repository;

import net.mohamed.devwebproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findByFarmerUserId(Long farmerId);
    List<Product> findByCategory(String category);
    List<Product> findByStockGreaterThan(int stock);
}
