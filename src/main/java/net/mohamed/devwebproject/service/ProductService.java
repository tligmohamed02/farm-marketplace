package net.mohamed.devwebproject.service;

import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Farmer;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.repository.FarmerRepository;
import net.mohamed.devwebproject.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FarmerRepository farmerRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public List<Product> getProductsByFarmer(Long farmerId) {
        return productRepository.findByFarmerUserId(farmerId);
    }

    public Product addProduct(Long farmerId, Product product) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Agriculteur non trouvé"));
        product.setFarmer(farmer);
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, Product productDetails) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setCategory(productDetails.getCategory());
        product.setImageUrl(productDetails.getImageUrl());

        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStockGreaterThan(0);
    }
}