package net.mohamed.devwebproject.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.dto.CartItem;
import net.mohamed.devwebproject.entity.Delivery;
import net.mohamed.devwebproject.entity.Order;
import net.mohamed.devwebproject.entity.OrderItem;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.service.CartService;
import net.mohamed.devwebproject.service.DeliveryService;
import net.mohamed.devwebproject.service.OrderService;
import net.mohamed.devwebproject.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final OrderService orderService;
    private final ProductService productService;
    private final CartService cartService;
    private final DeliveryService deliveryService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long customerId = (Long) session.getAttribute("userId");
        if (customerId == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("orders", orderService.getOrdersByCustomer(customerId));
        model.addAttribute("user", session.getAttribute("user"));
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "customer/dashboard";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }

        try {
            cartService.addToCart(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }

        List<CartItem> cart = cartService.getCart(session);
        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "customer/cart";
    }

    @PostMapping("/cart/update")
    public String updateCart(@RequestParam Long productId,
                             @RequestParam Integer quantity,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }

        try {
            if (quantity <= 0) {
                cartService.removeFromCart(session, productId);
                redirectAttributes.addFlashAttribute("success", "Produit retiré du panier");
            } else {
                cartService.updateQuantity(session, productId, quantity);
                redirectAttributes.addFlashAttribute("success", "Panier mis à jour");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/customer/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }

        cartService.removeFromCart(session, productId);
        redirectAttributes.addFlashAttribute("success", "Produit retiré du panier");
        return "redirect:/customer/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Long customerId = (Long) session.getAttribute("userId");
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/customer/cart";
        }

        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", cartService.getCartTotal(session));
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        model.addAttribute("deliveryPersons", deliveryService.getAvailableDeliveryPersons());
        return "customer/checkout";
    }

    @PostMapping("/checkout/confirm")
    public String confirmCheckout(@RequestParam(required = false) Long deliveryPersonId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        Long customerId = (Long) session.getAttribute("userId");
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Votre panier est vide");
            return "redirect:/customer/cart";
        }

        try {
            // Convertir les CartItems en OrderItems
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem cartItem : cart) {
                Product product = productService.getProductById(cartItem.getProductId());
                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setUnitPrice(cartItem.getPrice());
                orderItems.add(orderItem);
            }

            // Créer la commande
            Order order = orderService.placeOrder(customerId, orderItems);

            // Créer la livraison
            Delivery delivery = deliveryService.createDelivery(order, deliveryPersonId);
            order.setDelivery(delivery);

            // Vider le panier
            cartService.clearCart(session);

            redirectAttributes.addFlashAttribute("success",
                    "Commande #" + order.getOrderId() + " passée avec succès ! Tracking: " + delivery.getTrackingNumber());
            return "redirect:/customer/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur: " + e.getMessage());
            return "redirect:/customer/checkout";
        }
    }

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "customer/order-detail";
    }
}