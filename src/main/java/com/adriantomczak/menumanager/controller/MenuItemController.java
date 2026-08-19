package com.adriantomczak.menumanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adriantomczak.menumanager.model.MenuItem;
import com.adriantomczak.menumanager.service.MenuItemService;

import jakarta.validation.Valid;

/**
 * Handles the HTML pages and form submissions for menu items. Database and
 * business operations are delegated to MenuItemService.
 */
@Controller
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping("/")
    public String showMenu(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "") String notice,
            Model model) {

        populateHomepage(model, search, category, available);
        addNotice(model, notice);
        model.addAttribute("menuItem", new MenuItem());
        return "index";
    }

    @PostMapping("/menu-items")
    public String addMenuItem(
            @Valid @ModelAttribute("menuItem") MenuItem menuItem,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateHomepage(model, "", "", null);
            return "index";
        }

        menuItemService.create(menuItem);
        return "redirect:/?notice=created";
    }

    @GetMapping("/menu-items/{id}/edit")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        return menuItemService.findById(id)
                .map(menuItem -> {
                    model.addAttribute("menuItem", menuItem);
                    model.addAttribute("itemId", id);
                    return "edit-item";
                })
                .orElseGet(this::redirectForMissingItem);
    }

    @PostMapping("/menu-items/{id}/edit")
    public String updateMenuItem(
            @PathVariable Long id,
            @Valid @ModelAttribute("menuItem") MenuItem submittedItem,
            BindingResult bindingResult,
            Model model) {

        // Check first so an invalid form cannot display an edit page for a missing ID.
        if (menuItemService.findById(id).isEmpty()) {
            return redirectForMissingItem();
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("itemId", id);
            return "edit-item";
        }

        menuItemService.update(id, submittedItem);
        return "redirect:/?notice=updated";
    }

    @PostMapping("/menu-items/{id}/delete")
    public String deleteMenuItem(@PathVariable Long id) {

        if (menuItemService.delete(id)) {
            return "redirect:/?notice=deleted";
        }

        return redirectForMissingItem();
    }

    /**
     * Supplies the item list, dynamic category options and current filter values
     * whenever the homepage is rendered, including after validation errors.
     */
    private void populateHomepage(
            Model model,
            String search,
            String category,
            Boolean available) {

        String safeSearch = search == null ? "" : search;
        String safeCategory = category == null ? "" : category;

        model.addAttribute(
                "menuItems",
                menuItemService.findMenuItems(safeSearch, safeCategory, available));
        model.addAttribute("categories", menuItemService.findCategories());
        model.addAttribute("search", safeSearch);
        model.addAttribute("category", safeCategory);
        model.addAttribute("available", available);
        model.addAttribute(
                "filtersApplied",
                !safeSearch.isBlank()
                        || !safeCategory.isBlank()
                        || available != null);
    }

    /**
     * Notice codes provide friendly post-redirect messages without storing a
     * session. Unknown values are deliberately ignored.
     */
    private void addNotice(Model model, String notice) {
        switch (notice) {
            case "created" -> model.addAttribute(
                    "successMessage", "Menu item added successfully.");
            case "updated" -> model.addAttribute(
                    "successMessage", "Menu item updated successfully.");
            case "deleted" -> model.addAttribute(
                    "successMessage", "Menu item deleted successfully.");
            case "missing" -> model.addAttribute(
                    "warningMessage", "That menu item could not be found.");
            default -> {
                // No notice is required for an ordinary homepage request.
            }
        }
    }

    private String redirectForMissingItem() {
        return "redirect:/?notice=missing";
    }
}
