package net.mohamed.devwebproject.web;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Product;
import net.mohamed.devwebproject.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/farmer")
@RequiredArgsConstructor
public class FarmerController {
    private final ProductService productService;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/products/";

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
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile imageFile,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Long farmerId = (Long) session.getAttribute("userId");
        if (farmerId == null) {
            return "redirect:/auth/login";
        }

        try {
            // Upload de l'image
            if (!imageFile.isEmpty()) {
                String imageUrl = saveImage(imageFile);
                product.setImageUrl(imageUrl);
            }

            productService.addProduct(farmerId, product);
            redirectAttributes.addFlashAttribute("success", "Produit ajouté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout: " + e.getMessage());
        }

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
    public String editProduct(@PathVariable Long id,
                              @ModelAttribute Product product,
                              @RequestParam(value = "image", required = false) MultipartFile imageFile,
                              RedirectAttributes redirectAttributes) {
        try {
            Product existingProduct = productService.getProductById(id);

            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = saveImage(imageFile);
                product.setImageUrl(imageUrl);
            } else {
                product.setImageUrl(existingProduct.getImageUrl());
            }

            productService.updateProduct(id, product);
            redirectAttributes.addFlashAttribute("success", "Produit modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification: " + e.getMessage());
        }

        return "redirect:/farmer/dashboard";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Produit supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/farmer/dashboard";
    }

    private String saveImage(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/products/" + filename;
    }
}