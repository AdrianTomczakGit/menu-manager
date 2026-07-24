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
}