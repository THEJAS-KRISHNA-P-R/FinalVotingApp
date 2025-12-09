package com.spring.tkpr;  // ← MUST BE com.polling (not com.spring.tkpr)

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PollingService service;

    @GetMapping("/login")
    public String loginForm() {
        return "admLogin";  // your admin login page
    }

    @PostMapping("/login")
    public String login(@RequestParam String adminName,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (service.adminLogin(adminName, password)) {
            session.setAttribute("adminName", adminName);
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("error", "Wrong credentials");
        return "admLogin";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("adminName") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("nominees", service.getAllNominees());
        return "admDashboard";  // your admin dashboard page
    }

    @PostMapping("/add-nominee")
    public String addNominee(@RequestParam String name,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (session.getAttribute("adminName") == null) {
            return "redirect:/admin/login";
        }

        String trimmedName = name.trim();
        if (trimmedName.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Nominee name cannot be empty!");
            return "redirect:/admin/dashboard";
        }

        Nominee added = service.addNominee(trimmedName);
        if (added == null) {
            redirectAttributes.addFlashAttribute("error", "Nominee already exists!");
            return "redirect:/admin/dashboard";
        }

        redirectAttributes.addFlashAttribute("success", "Nominee added successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/delete-nominee")
    public String deleteNominee(@RequestParam Long id, HttpSession session) {
        if (session.getAttribute("adminName") == null) {
            return "redirect:/admin/login";
        }
        service.deleteNominee(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}