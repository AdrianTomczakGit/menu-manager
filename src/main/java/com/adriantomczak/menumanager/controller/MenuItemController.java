// Defines the package this controller belongs to.
package com.adriantomczak.menumanager.controller;

// Imports the MenuItem entity.
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.adriantomczak.menumanager.model.MenuItem;
import com.adriantomczak.menumanager.repository.MenuItemRepository;

import jakarta.validation.Valid;

/*
 * This controller handles browser requests relating
 * to menu items.
 *
 * It allows users to:
 * - View menu items
 * - Add menu items
 * - Edit menu items
 * - Delete menu items
 */
@Controller
public class MenuItemController {

    /*
     * Stores the repository used to communicate
     * with the menu-item database table.
     */
    private final MenuItemRepository menuItemRepository;

    /*
     * Constructor injection.
     *
     * Spring automatically supplies the repository
     * when it creates this controller.
     */
    public MenuItemController(
            MenuItemRepository menuItemRepository) {

        this.menuItemRepository = menuItemRepository;
    }

    /*
     * Displays the homepage.
     *
     * Example address:
     * http://localhost:8080/
     */
    @GetMapping("/")
    public String showMenu(Model model) {

        /*
         * Retrieves every menu item from the database
         * and sends the list to index.html.
         */
        model.addAttribute(
                "menuItems",
                menuItemRepository.findAll()
        );

        /*
         * Creates an empty MenuItem object for
         * the add-item form.
         */
        model.addAttribute(
                "menuItem",
                new MenuItem()
        );

        /*
         * Displays:
         * src/main/resources/templates/index.html
         */
        return "index";
    }

    /*
     * Processes the form used to add a new menu item.
     *
     * The browser sends:
     * POST /menu-items
     */
    @PostMapping("/menu-items")
    public String addMenuItem(

            /*
             * @ModelAttribute connects the submitted form
             * values to a MenuItem object.
             *
             * @Valid runs the validation annotations
             * inside MenuItem.java.
             */
            @Valid
            @ModelAttribute("menuItem")
            MenuItem menuItem,

            /*
             * Contains validation errors.
             *
             * BindingResult must appear immediately after
             * the object marked with @Valid.
             */
            BindingResult bindingResult,

            /*
             * Allows data to be sent back to the HTML
             * page when validation fails.
             */
            Model model) {

        /*
         * If validation fails, do not save the item.
         */
        if (bindingResult.hasErrors()) {

            /*
             * Reloads the existing menu items so they
             * still appear underneath the form.
             */
            model.addAttribute(
                    "menuItems",
                    menuItemRepository.findAll()
            );

            /*
             * Displays the homepage again with the
             * validation messages.
             */
            return "index";
        }

        /*
         * Saves the valid menu item in the database.
         */
        menuItemRepository.save(menuItem);

        /*
         * Redirects the browser to the homepage.
         */
        return "redirect:/";
    }

    /*
     * Displays the edit form for a specific menu item.
     *
     * Example address:
     * http://localhost:8080/menu-items/3/edit
     */
    @GetMapping("/menu-items/{id}/edit")
    public String showEditForm(

            // Reads the menu-item ID from the URL.
            @PathVariable Long id,

            // Allows information to be sent to the edit page.
            Model model) {

        /*
         * Searches the database for the selected item.
         *
         * If no matching item exists, the result becomes null.
         */
        MenuItem menuItem = menuItemRepository
                .findById(id)
                .orElse(null);

        /*
         * Returns to the homepage when the supplied
         * database ID does not exist.
         */
        if (menuItem == null) {
            return "redirect:/";
        }

        /*
         * Sends the existing menu item to the edit form.
         *
         * Its current values will appear inside the inputs.
         */
        model.addAttribute(
                "menuItem",
                menuItem
        );

        /*
         * Sends the ID separately.
         *
         * This keeps the correct form address available
         * if the submitted edit fails validation.
         */
        model.addAttribute(
                "itemId",
                id
        );

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

            // Reads the selected item's ID from the URL.
            @PathVariable Long id,

            /*
             * Converts the submitted form fields into
             * a MenuItem object and runs validation.
             */
            @Valid
            @ModelAttribute("menuItem")
            MenuItem submittedItem,

            /*
             * Contains validation or conversion errors.
             */
            BindingResult bindingResult,

            /*
             * Allows information to be returned
             * to the edit page.
             */
            Model model) {

        /*
         * When validation fails, return to the edit form
         * without changing the database record.
         */
        if (bindingResult.hasErrors()) {

            /*
             * Keeps the selected ID available to the form.
             */
            model.addAttribute(
                    "itemId",
                    id
            );

            /*
             * Displays the edit page with validation errors.
             */
            return "edit-item";
        }

        /*
         * Finds the existing database record.
         */
        MenuItem existingItem = menuItemRepository
                .findById(id)
                .orElse(null);

        /*
         * Returns home if the record no longer exists.
         */
        if (existingItem == null) {
            return "redirect:/";
        }

        /*
         * Copies the validated submitted values into
         * the existing database entity.
         */
        existingItem.setName(
                submittedItem.getName()
        );

        existingItem.setDescription(
                submittedItem.getDescription()
        );

        existingItem.setPrice(
                submittedItem.getPrice()
        );

        existingItem.setCategory(
                submittedItem.getCategory()
        );

        existingItem.setAvailable(
                submittedItem.isAvailable()
        );

        /*
         * Saves the changed entity.
         *
         * Because it already has an ID, JPA updates
         * the existing record rather than adding a new one.
         */
        menuItemRepository.save(existingItem);

        /*
         * Redirects the browser to the homepage.
         */
        return "redirect:/";
    }

    /*
     * Deletes a specific menu item.
     *
     * Example request:
     * POST /menu-items/3/delete
     */
    @PostMapping("/menu-items/{id}/delete")
    public String deleteMenuItem(

            // Reads the selected item's ID from the URL.
            @PathVariable Long id) {

        /*
         * Checks that the database record exists before
         * attempting to delete it.
         */
        if (menuItemRepository.existsById(id)) {

            /*
             * Deletes the matching menu item.
             */
            menuItemRepository.deleteById(id);
        }

        /*
         * Redirects the browser back to the homepage.
         */
        return "redirect:/";
    }
}