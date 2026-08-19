package com.adriantomczak.menumanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class MenuItemValidationTests {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void validMenuItemHasNoValidationErrors() {
        MenuItem menuItem = validMenuItem();

        assertTrue(validator.validate(menuItem).isEmpty());
    }

    @Test
    void requiredFieldsAreValidated() {
        MenuItem menuItem = new MenuItem("  ", null, null, "", true);

        assertEquals(
                Set.of("name", "price", "category"),
                invalidProperties(menuItem));
    }

    @Test
    void fieldLengthLimitsAreValidated() {
        MenuItem menuItem = new MenuItem(
                "n".repeat(101),
                "d".repeat(301),
                BigDecimal.ONE,
                "c".repeat(51),
                true);

        assertEquals(
                Set.of("name", "description", "category"),
                invalidProperties(menuItem));
    }

    @Test
    void priceMustBeAtLeastOnePenny() {
        MenuItem menuItem = validMenuItem();
        menuItem.setPrice(BigDecimal.ZERO);

        assertEquals(Set.of("price"), invalidProperties(menuItem));
    }

    private Set<String> invalidProperties(MenuItem menuItem) {
        return validator.validate(menuItem).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    private MenuItem validMenuItem() {
        return new MenuItem(
                "Chicken Burger",
                "Grilled chicken with salad",
                new BigDecimal("8.95"),
                "Burgers",
                true);
    }
}
