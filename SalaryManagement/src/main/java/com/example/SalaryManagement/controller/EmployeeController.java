package com.example.salarymanagement.controller;

import com.example.salarymanagement.entity.Employee;
import com.example.salarymanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService service;

    @GetMapping("/")
    public String index(Model model, @RequestParam(defaultValue = "") String search) {
        List<Employee> employees = search.isEmpty() ? service.findAll() : service.searchByName(search);
        model.addAttribute("employees", employees);
        model.addAttribute("employee", new Employee());  // Form mới
        model.addAttribute("search", search);
        return "index";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Employee employee, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("employees", service.findAll());
            model.addAttribute("errorMessage", "Please fix the errors below.");
            return "index";
        }

        if (service.findAll().stream().anyMatch(e -> e.getName().equals(employee.getName()) && !e.getId().equals(employee.getId()))) {
            model.addAttribute("employees", service.findAll());
            model.addAttribute("errorMessage", "Unable to create. A User with Name already exist.");
            return "index";
        }

        service.save(employee);
        redirect.addFlashAttribute("successMessage", employee.getId() == null ? "User created successfully." : "User updated successfully.");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        service.findById(id).ifPresent(e -> model.addAttribute("employee", e));
        model.addAttribute("employees", service.findAll());
        return "index";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        service.deleteById(id);
        redirect.addFlashAttribute("successMessage", "User deleted successfully.");
        return "redirect:/";
    }
}