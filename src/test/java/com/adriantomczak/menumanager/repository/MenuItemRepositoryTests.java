package com.adriantomczak.menumanager.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.adriantomczak.menumanager.model.MenuItem;

@DataJpaTest
class MenuItemRepositoryTests {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Test
    void savesAndFindsMenuItem() {
        MenuItem savedItem = menuItemRepository.saveAndFlush(menuItem(
                "Tomato Soup", "Starters", true));

        assertNotNull(savedItem.getId());
        assertEquals(
                "Tomato Soup",
                menuItemRepository.findById(savedItem.getId()).orElseThrow().getName());
    }

    @Test
    void filtersByNameCategoryAndAvailabilityTogether() {
        menuItemRepository.saveAllAndFlush(List.of(
                menuItem("Chicken Burger", "Burgers", true),
                menuItem("Chicken Wrap", "Wraps", true),
                menuItem("Double Burger", "Burgers", false)));

        List<MenuItem> matches = menuItemRepository.findFiltered(
                "CHICKEN", "burgers", true);

        assertEquals(1, matches.size());
        assertEquals("Chicken Burger", matches.get(0).getName());
        assertEquals(
                List.of("Burgers", "Wraps"),
                menuItemRepository.findDistinctCategories());
    }

    private MenuItem menuItem(String name, String category, boolean available) {
        return new MenuItem(
                name,
                "Test description",
                new BigDecimal("6.50"),
                category,
                available);
    }
}
