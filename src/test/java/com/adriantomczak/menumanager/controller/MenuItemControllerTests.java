package com.adriantomczak.menumanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.adriantomczak.menumanager.model.MenuItem;
import com.adriantomczak.menumanager.repository.MenuItemRepository;

@SpringBootTest
@AutoConfigureMockMvc
class MenuItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @BeforeEach
    void clearDatabase() {
        menuItemRepository.deleteAll();
    }

    @Test
    void homepageReturnsSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists(
                        "menuItem", "menuItems", "categories"));
    }

    @Test
    void createsMenuItemFromValidForm() throws Exception {
        mockMvc.perform(post("/menu-items")
                        .param("name", "Tomato Soup")
                        .param("description", "Served with bread")
                        .param("price", "5.25")
                        .param("category", "Starters")
                        .param("available", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?notice=created"));

        assertEquals(1, menuItemRepository.count());
        assertEquals("Tomato Soup", menuItemRepository.findAll().get(0).getName());
    }

    @Test
    void invalidCreateReturnsValidationErrorsWithoutSaving() throws Exception {
        mockMvc.perform(post("/menu-items")
                        .param("name", "")
                        .param("price", "0.00")
                        .param("category", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors(
                        "menuItem", "name", "price", "category"));

        assertEquals(0, menuItemRepository.count());
    }

    @Test
    void invalidEditReturnsValidationErrorsWithoutChangingItem() throws Exception {
        MenuItem savedItem = menuItemRepository.save(new MenuItem(
                "Cola", null, new BigDecimal("2.00"), "Drinks", true));

        mockMvc.perform(post("/menu-items/{id}/edit", savedItem.getId())
                        .param("name", "")
                        .param("price", "0.00")
                        .param("category", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-item"))
                .andExpect(model().attributeHasFieldErrors(
                        "menuItem", "name", "price", "category"));

        assertEquals(
                "Cola",
                menuItemRepository.findById(savedItem.getId()).orElseThrow().getName());
    }

    @Test
    void editsThenDeletesExistingItem() throws Exception {
        MenuItem savedItem = menuItemRepository.save(new MenuItem(
                "Cola", null, new BigDecimal("2.00"), "Drinks", true));

        mockMvc.perform(post("/menu-items/{id}/edit", savedItem.getId())
                        .param("name", "Diet Cola")
                        .param("description", "Sugar free")
                        .param("price", "2.25")
                        .param("category", "Drinks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?notice=updated"));

        MenuItem editedItem = menuItemRepository.findById(savedItem.getId()).orElseThrow();
        assertEquals("Diet Cola", editedItem.getName());
        assertFalse(editedItem.isAvailable());

        mockMvc.perform(post("/menu-items/{id}/delete", savedItem.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?notice=deleted"));

        assertEquals(0, menuItemRepository.count());
    }

    @Test
    void missingEditRedirectsSafely() throws Exception {
        mockMvc.perform(get("/menu-items/999999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?notice=missing"));
    }
}
