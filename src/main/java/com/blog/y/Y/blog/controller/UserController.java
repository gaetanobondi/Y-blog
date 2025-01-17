package com.blog.y.Y.blog.controller;

import com.blog.y.Y.blog.model.User;
import com.blog.y.Y.blog.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public String createUser(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("repeat_password") String repeatPassword, Model model) {
            String error = "";
        // Controlla se le password corrispondono
        if (!password.equals(repeatPassword)) {
            error = "Le password non corrispondono.";
            ResponseEntity.badRequest().body(error);
            model.addAttribute("error", error);
            return "signup";
        }

        // Controlla se l'email esiste già
        if (userRepo.getUserByUsername(username) != null) {
            error = "Esiste già un utente registrato con questo username.";
            ResponseEntity.badRequest().body(error);
            model.addAttribute("error", error);
            return "signup";
        }

        // Crea un nuovo utente
        User user = new User();
        user.setUsername(username);
        // Crittografa la password
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword); // Cripta la password
        user.setCreatedAt(LocalDateTime.now()); // Imposta la data di registrazione

        userRepo.save(user);

        // Risposta con successo
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Authentication authentication) {
        // Verifica se l'utente è già autenticato
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home"; // Reindirizza alla home se già loggato
        }
        return "signup";
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {
        // Verifica se l'utente è già autenticato
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home"; // Reindirizza alla home se già loggato
        }
        return "login";
    }


}
