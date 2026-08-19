package com.adriantomczak.menumanager.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adriantomczak.menumanager.model.MenuItem;
import com.adriantomczak.menumanager.repository.MenuItemRepository;

/**
 * Keeps menu-item business logic between the web controller and repository.
 * The controller handles HTTP concerns; this class handles data preparation.
 */
@Service
@Transactional(readOnly = true)
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItem> findMenuItems(
            String search,
            String category,
            Boolean available) {

        return menuItemRepository.findFiltered(
                normalizeFilter(search),
                normalizeFilter(category),
                available);
    }

    /**
     * Removes blank values and case-insensitive duplicates from the category
     * options while retaining the spelling stored in the database.
     */
    public List<String> findCategories() {
        Map<String, String> categoriesByLowercaseName = new TreeMap<>();

        menuItemRepository.findDistinctCategories().stream()
                .filter(category -> category != null && !category.isBlank())
                .map(String::trim)
                .forEach(category -> categoriesByLowercaseName.putIfAbsent(
                        category.toLowerCase(Locale.ROOT),
                        category));

        return List.copyOf(categoriesByLowercaseName.values());
    }

    public Optional<MenuItem> findById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Transactional
    public MenuItem create(MenuItem menuItem) {
        normalizeMenuItem(menuItem);
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public boolean update(Long id, MenuItem submittedItem) {
        Optional<MenuItem> existingItem = menuItemRepository.findById(id);

        if (existingItem.isEmpty()) {
            return false;
        }

        MenuItem menuItem = existingItem.get();
        copyEditableFields(submittedItem, menuItem);
        normalizeMenuItem(menuItem);
        menuItemRepository.save(menuItem);
        return true;
    }

    @Transactional
    public boolean delete(Long id) {
        if (!menuItemRepository.existsById(id)) {
            return false;
        }

        menuItemRepository.deleteById(id);
        return true;
    }

    private void copyEditableFields(MenuItem source, MenuItem target) {
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setPrice(source.getPrice());
        target.setCategory(source.getCategory());
        target.setAvailable(source.isAvailable());
    }

    // Trimming in one place keeps values tidy for both create and edit flows.
    private void normalizeMenuItem(MenuItem menuItem) {
        menuItem.setName(menuItem.getName().trim());
        menuItem.setCategory(menuItem.getCategory().trim());

        if (menuItem.getDescription() == null
                || menuItem.getDescription().isBlank()) {
            menuItem.setDescription(null);
        } else {
            menuItem.setDescription(menuItem.getDescription().trim());
        }
    }

    private String normalizeFilter(String value) {
        return value == null ? "" : value.trim();
    }
}
