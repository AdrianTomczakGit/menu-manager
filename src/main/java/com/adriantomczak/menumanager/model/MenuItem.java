package com.adriantomczak.menumanager.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A menu item stored by JPA in the application's H2 database.
 * BigDecimal is used for prices to avoid floating-point rounding errors.
 */
@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name cannot exceed 100 characters.")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 300, message = "Description cannot exceed 300 characters.")
    @Column(length = 300)
    private String description;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01.")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotBlank(message = "Category is required.")
    @Size(max = 50, message = "Category cannot exceed 50 characters.")
    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private boolean available;

    // JPA and Spring's form binding require a no-argument constructor.
    public MenuItem() {
    }

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

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
