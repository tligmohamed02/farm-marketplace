package net.mohamed.devwebproject.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.service.CartService;
import net.mohamed.devwebproject.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final CartService cartService;

    @GetMapping
    public String listProducts(HttpSession session, Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "products/list";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, HttpSession session, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("cartCount", cartService.getCartItemCount(session));
        return "products/detail";
    }

    @PostMapping("/cart/add")
    public String addToCartPublic(@RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") Integer quantity,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Vérifier si l'utilisateur est connecté
        if (session.getAttribute("userId") == null) {
            redirectAttributes.addFlashAttribute("error", "Vous devez être connecté pour ajouter au panier");
            return "redirect:/auth/login";
        }

        try {
            cartService.addToCart(session, productId, quantity);
            redirectAttributes.addFlashAttribute("success", "Produit ajouté au panier !");
            return "redirect:/products";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/products";
        }
    }
}