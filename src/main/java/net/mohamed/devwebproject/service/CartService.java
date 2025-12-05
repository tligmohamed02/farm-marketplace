package net.mohamed.devwebproject.service;
import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.dto.CartItem;
import net.mohamed.devwebproject.entity.Product;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ProductService productService;
    private static final String CART_SESSION_KEY = "cart";

    @SuppressWarnings("unchecked")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(HttpSession session, Long productId, Integer quantity) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Produit non trouvé");
        }

        List<CartItem> cart = getCart(session);

        // Vérifier si le produit existe déjà dans le panier
        CartItem existingItem = cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(
                    product.getProductId(),
                    product.getName(),
                    product.getImageUrl(),
                    product.getPrice(),
                    quantity,
                    product.getCategory()
            );
            cart.add(newItem);
        }

        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void updateQuantity(HttpSession session, Long productId, Integer quantity) {
        List<CartItem> cart = getCart(session);
        cart.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeFromCart(HttpSession session, Long productId) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart(HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }

    public Double getCartTotal(HttpSession session) {
        return getCart(session).stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public Integer getCartItemCount(HttpSession session) {
        return getCart(session).stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
