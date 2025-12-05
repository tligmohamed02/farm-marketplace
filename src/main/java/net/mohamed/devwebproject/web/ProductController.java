package net.mohamed.devwebproject.web;

import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAvailableProducts());
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<Product>> getProductsByFarmer(@PathVariable Long farmerId) {
        return ResponseEntity.ok(productService.getProductsByFarmer(farmerId));
    }

    @PostMapping("/farmer/{farmerId}")
    public ResponseEntity<Product> addProduct(@PathVariable Long farmerId, @RequestBody Product product) {
        return ResponseEntity.ok(productService.addProduct(farmerId, product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(productId, product));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted");
    }
}
