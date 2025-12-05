package net.mohamed.devwebproject.web;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.mohamed.devwebproject.entity.Customer;
import net.mohamed.devwebproject.entity.Farmer;
import net.mohamed.devwebproject.entity.User;
import net.mohamed.devwebproject.service.AuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = authService.login(email, password);
            if (user != null) {
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserId());

                if (user instanceof Farmer) {
                    session.setAttribute("userType", "farmer");
                    return "redirect:/farmer/dashboard";
                } else if (user instanceof Customer) {
                    session.setAttribute("userType", "customer");
                    return "redirect:/customer/dashboard";
                } else {
                    session.setAttribute("userType", "admin");
                    return "redirect:/admin/dashboard";
                }
            }
            model.addAttribute("error", "Email ou mot de passe incorrect");
            return "auth/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String userType,
                           @RequestParam(required = false) String location,
                           @RequestParam(required = false) String farmName,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) String phone,
                           Model model) {
        User user;
        if ("farmer".equals(userType)) {
            Farmer farmer = new Farmer();
            farmer.setName(name);
            farmer.setEmail(email);
            farmer.setLocation(location);
            farmer.setFarmName(farmName);
            user = farmer;
        } else {
            Customer customer = new Customer();
            customer.setName(name);
            customer.setEmail(email);
            customer.setShippingAddress(address);
            customer.setPhone(phone);
            user = customer;
        }

        try {
            authService.registerUser(user, password);
            return "redirect:/auth/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de l'inscription");
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
