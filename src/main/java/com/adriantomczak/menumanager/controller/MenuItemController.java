// Defines which package this class belongs to.
// The folder structure must match this package.
package com.adriantomczak.menumanager.controller;

// Imports the MenuItem entity that represents one menu item.
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.adriantomczak.menumanager.model.MenuItem;
import com.adriantomczak.menumanager.repository.MenuItemRepository;



@Controller
public class MenuItemController {

    // Stores a reference to the database repository.
    private final MenuItemRepository menuItemRepository;

    /*
     * Constructor injection:
     * Spring automatically provides the MenuItemRepository
     * when it creates this controller.
     */
    public MenuItemController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /*
     * Handles a GET request to the homepage.
     *
     * Example:
     * http://localhost:8080/
     */
    @GetMapping("/")
    public String showMenu(Model model) {

        /*
         * Retrieves every menu item from the database
         * and sends the list to the HTML page.
         */
        model.addAttribute(
                "menuItems",
                menuItemRepository.findAll()
        );

        /*
         * Creates an empty MenuItem object.
         * The HTML form will fill this object with the user's input.
         */
        model.addAttribute(
                "menuItem",
                new MenuItem()
        );

        /*
         * Tells Spring to display:
         * src/main/resources/templates/index.html
         */
        return "index";
    }

    /*
     * Handles the form submission.
     *
     * The form sends a POST request to:
     * /menu-items
     */
    @PostMapping("/menu-items")
    public String addMenuItem(
            @ModelAttribute MenuItem menuItem) {

        /*
         * Saves the submitted menu item into the H2 database.
         *
         * If the item has no ID, JPA treats it as a new record.
         */
        menuItemRepository.save(menuItem);

        /*
         * Redirects the browser back to the homepage.
         * This refreshes the displayed menu-item list.
         */
        return "redirect:/";
    }

    /*
 * Handles a POST request for deleting a specific menu item.
 *
 * Example request:
 * /menu-items/3/delete
 *
 * The number 3 is the ID of the menu item.
 */
@PostMapping("/menu-items/{id}/delete")
public String deleteMenuItem(@PathVariable Long id) {

    /*
     * Checks that the menu item exists before trying to delete it.
     * This avoids an error if an invalid ID is supplied.
     */
    if (menuItemRepository.existsById(id)) {

        // Deletes the database record with the matching ID.
        menuItemRepository.deleteById(id);
    }

    /*
     * Redirects the browser back to the homepage
     * so the updated list is displayed.
     */
    return "redirect:/";
}

/*
 * Displays the editing page for one menu item.
 *
 * Example address:
 * http://localhost:8080/menu-items/3/edit
 *
 * The number 3 represents the database ID.
 */
@GetMapping("/menu-items/{id}/edit")
public String showEditForm(
        @PathVariable Long id,
        Model model) {

    /*
     * Searches the database for the menu item.
     *
     * findById() returns an Optional because the item
     * might not exist.
     *
     * orElse(null) gives us null when no item is found.
     */
    MenuItem menuItem = menuItemRepository
            .findById(id)
            .orElse(null);

    /*
     * If the item does not exist, return the user
     * to the homepage instead of displaying an error.
     */
    if (menuItem == null) {
        return "redirect:/";
    }

    /*
     * Sends the existing menu item to the HTML page.
     *
     * The form will begin with the item's current
     * name, description, price and category.
     */
    model.addAttribute("menuItem", menuItem);

    /*
     * Displays:
     * src/main/resources/templates/edit-item.html
     */
    return "edit-item";

    
}

/*
 * Processes the edit form after the user presses
 * the Save Changes button.
 *
 * Example request:
 * POST /menu-items/3/edit
 */
@PostMapping("/menu-items/{id}/edit")
public String updateMenuItem(
        @PathVariable Long id,
        @ModelAttribute MenuItem submittedItem) {

    /*
     * Retrieves the existing database record.
     *
     * We update the existing object instead of creating
     * a completely new record.
     */
    MenuItem existingItem = menuItemRepository
            .findById(id)
            .orElse(null);

    /*
     * If the selected item no longer exists,
     * return the user to the homepage.
     */
    if (existingItem == null) {
        return "redirect:/";
    }

    /*
     * Copies the values submitted through the form
     * into the existing database entity.
     */
    existingItem.setName(submittedItem.getName());
    existingItem.setDescription(submittedItem.getDescription());
    existingItem.setPrice(submittedItem.getPrice());
    existingItem.setCategory(submittedItem.getCategory());
    existingItem.setAvailable(submittedItem.isAvailable());

    /*
     * Saves the updated entity.
     *
     * Because existingItem already has a database ID,
     * JPA updates the record rather than creating another one.
     */
    menuItemRepository.save(existingItem);

    /*
     * Returns the user to the homepage where the
     * updated information will be displayed.
     */
    return "redirect:/";
}
}