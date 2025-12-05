package net.mohamed.devwebproject.service;

import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Customer;
import net.mohamed.devwebproject.entity.Order;
import net.mohamed.devwebproject.entity.OrderItem;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.enums.OrderStatus;
import net.mohamed.devwebproject.repository.CustomerRepository;
import net.mohamed.devwebproject.repository.OrderRepository;
import net.mohamed.devwebproject.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Transactional
    public Order placeOrder(Long customerId, List<OrderItem> items) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Client non trouvé"));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuffisant pour: " + product.getName());
            }

            product.updateStock(-item.getQuantity());
            productRepository.save(product);

            item.setOrder(order);
            item.setUnitPrice(product.getPrice());
        }

        order.setOrderItems(items);
        order.calculateTotal();

        return orderRepository.save(order);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerUserId(customerId);
    }

    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        order.setStatus(status);
        return orderRepository.save(order);
    }
}