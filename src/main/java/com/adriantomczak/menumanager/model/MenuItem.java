// Defines the package this class belongs to.
// The package must match the file's folder structure.
package com.adriantomczak.menumanager.model;

// BigDecimal is used for prices because it is more accurate for money than double.
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/*
 * Marks this Java class as a database entity.
 *
 * Hibernate will create a database table based on this class.
 */
@Entity
public class MenuItem {

    /*
     * The unique database ID for each menu item.
     *
     * @Id marks this field as the primary key.
     *
     * @GeneratedValue tells the database to create IDs
     * automatically, such as 1, 2, 3 and so on.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * The name cannot be empty or contain only spaces.
     *
     * The maximum permitted length is 100 characters.
     */
    @NotBlank(message = "Name is required.")
    @Size(
        max = 100,
        message = "Name cannot exceed 100 characters."
    )
    private String name;

    /*
     * The description is optional.
     *
     * When supplied, it cannot exceed 300 characters.
     */
    @Size(
        max = 300,
        message = "Description cannot exceed 300 characters."
    )
    private String description;

    /*
     * A price must be provided.
     *
     * The minimum permitted price is £0.01.
     */
    @NotNull(message = "Price is required.")
    @DecimalMin(
        value = "0.01",
        message = "Price must be at least £0.01."
    )
    private BigDecimal price;

    /*
     * The category cannot be empty or contain only spaces.
     *
     * The maximum permitted length is 50 characters.
     */
    @NotBlank(message = "Category is required.")
    @Size(
        max = 50,
        message = "Category cannot exceed 50 characters."
    )
    private String category;

    /*
     * Stores whether the item is currently available.
     *
     * true means available.
     * false means unavailable.
     */
    private boolean available;

    /*
     * Empty constructor required by JPA and Hibernate.
     *
     * Spring also uses this constructor when converting
     * submitted form fields into a MenuItem object.
     */
    public MenuItem() {
    }

    /*
     * Constructor used when creating a MenuItem manually.
     *
     * The ID is not included because the database
     * generates it automatically.
     */
    public MenuItem(
            String name,
            String description,
            BigDecimal price,
            String category,
            boolean available) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    /*
     * Returns the menu item's database ID.
     */
    public Long getId() {
        return id;
    }

    /*
     * Returns the menu item's name.
     */
    public String getName() {
        return name;
    }

    /*
     * Updates the menu item's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * Returns the menu item's description.
     */
    public String getDescription() {
        return description;
    }

    /*
     * Updates the menu item's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /*
     * Returns the menu item's price.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /*
     * Updates the menu item's price.
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /*
     * Returns the menu item's category.
     */
    public String getCategory() {
        return category;
    }

    /*
     * Updates the menu item's category.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /*
     * Returns whether the item is available.
     *
     * Boolean getters commonly begin with "is"
     * rather than "get".
     */
    public boolean isAvailable() {
        return available;
    }

    /*
     * Updates whether the item is available.
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
}