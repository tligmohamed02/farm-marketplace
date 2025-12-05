package net.mohamed.devwebproject.web;

import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.service.OrderService;
import net.mohamed.devwebproject.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("products", productService.getAllProducts());
        return "admin/dashboard";
    }
}
