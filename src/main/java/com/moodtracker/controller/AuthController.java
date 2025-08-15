package com.moodtracker.controller;

import com.moodtracker.model.User;
import com.moodtracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userRepo.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Login gagal");
            return "login";
        }
        return "redirect:/dashboard?username=" + username;
    }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String password, @RequestParam String gender, Model model) {
        if (userRepo.findByUsername(username) != null) {
            model.addAttribute("error", "Username sudah terdaftar");
            return "register";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setGender(gender);
        userRepo.save(user);
        model.addAttribute("success", "Registrasi berhasil, silakan login");
        return "login";
    }
}