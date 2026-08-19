package com.adriantomczak.menumanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.adriantomczak.menumanager.model.MenuItem;
import com.adriantomczak.menumanager.repository.MenuItemRepository;

@SpringBootTest
@Transactional
class MenuItemServiceTests {

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @BeforeEach
    void clearDatabase() {
        menuItemRepository.deleteAll();
    }

    @Test
    void createTrimsTextAndConvertsBlankDescriptionToNull() {
        MenuItem createdItem = menuItemService.create(new MenuItem(
                "  Lemonade  ",
                "   ",
                new BigDecimal("2.50"),
                "  Drinks  ",
                true));

        assertEquals("Lemonade", createdItem.getName());
        assertEquals("Drinks", createdItem.getCategory());
        assertNull(createdItem.getDescription());
    }

    @Test
    void updateAndDeleteReturnWhetherTheItemExists() {
        MenuItem savedItem = menuItemService.create(new MenuItem(
                "Fries", null, new BigDecimal("3.00"), "Sides", true));
        MenuItem editedItem = new MenuItem(
                "Loaded Fries",
                "Cheese and spring onion",
                new BigDecimal("4.50"),
                "Sides",
                false);

        assertTrue(menuItemService.update(savedItem.getId(), editedItem));
        assertEquals(
                "Loaded Fries",
                menuItemService.findById(savedItem.getId()).orElseThrow().getName());
        assertFalse(menuItemService.findById(savedItem.getId()).orElseThrow().isAvailable());

        assertTrue(menuItemService.delete(savedItem.getId()));
        assertFalse(menuItemService.delete(savedItem.getId()));
    }
}
