package net.mohamed.devwebproject.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/farmer")
@RequiredArgsConstructor
public class FarmerController {
    private final ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long farmerId = (Long) session.getAttribute("userId");
        if (farmerId == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("products", productService.getProductsByFarmer(farmerId));
        model.addAttribute("user", session.getAttribute("user"));
        return "farmer/dashboard";
    }

    @GetMapping("/products/add")
    public String addProductPage(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("product", new Product());
        return "farmer/add-product";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute Product product, HttpSession session) {
        Long farmerId = (Long) session.getAttribute("userId");
        if (farmerId == null) {
            return "redirect:/auth/login";
        }
        productService.addProduct(farmerId, product);
        return "redirect:/farmer/dashboard";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/auth/login";
        }
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/farmer/dashboard";
        }
        model.addAttribute("product", product);
        return "farmer/edit-product";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product) {
        productService.updateProduct(id, product);
        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/farmer/dashboard";
    }
}

