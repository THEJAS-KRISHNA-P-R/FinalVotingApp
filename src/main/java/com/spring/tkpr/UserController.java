package com.spring.tkpr;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired private PollingService service;

    @GetMapping("/login")
    public String loginForm() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String email,
                        @RequestParam String password,
                        HttpSession session, Model model) {
        return service.loginUser(username, email, password)
                .map(u -> {
                    session.setAttribute("user", u);
                    return "redirect:/user/dashboard";
                })
                .orElseGet(() -> {
                    model.addAttribute("error", "Invalid credentials");
                    return "login";
                });
    }

    // THIS METHOD WAS BROKEN BEFORE — NOW FIXED
    @GetMapping("/signup")
    public String signupForm(Model model) {
        if (!model.containsAttribute("signupRequest")) {
            model.addAttribute("signupRequest", new SignupRequest());
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupRequest") SignupRequest request,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            return "signup";  // ← goes back with red errors
        }

        boolean success = service.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        if (!success) {
            model.addAttribute("error", "Username or email already taken");
            return "signup";
        }

        model.addAttribute("message", "Account created successfully! Please login.");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page,
                            HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/user/login";

        Page<Nominee> p = service.getNominees(page, 5);
        model.addAttribute("nominees", p.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", p.getTotalPages());
        return "dashboard";
    }

    @PostMapping("/vote")
    public String vote(@RequestParam Long nomineeId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && service.vote(user.getId(), nomineeId)) {
            user = service.getUserById(user.getId()).orElse(user);
            session.setAttribute("user", user);
        }
        return "redirect:/user/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/landing";
    }
}