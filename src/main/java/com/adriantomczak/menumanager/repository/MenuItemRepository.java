package com.adriantomczak.menumanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adriantomczak.menumanager.model.MenuItem;

/**
 * Provides database access for menu items. Spring Data supplies the standard
 * CRUD operations, while the queries below support the homepage filters.
 */
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("""
            SELECT item
            FROM MenuItem item
            WHERE (:search = ''
                    OR LOWER(item.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:category = ''
                    OR LOWER(item.category) = LOWER(:category))
              AND (:available IS NULL OR item.available = :available)
            ORDER BY LOWER(item.name), item.id
            """)
    List<MenuItem> findFiltered(
            @Param("search") String search,
            @Param("category") String category,
            @Param("available") Boolean available);

    // Category choices come from stored data rather than a hard-coded list.
    @Query("""
            SELECT DISTINCT item.category
            FROM MenuItem item
            ORDER BY item.category
            """)
    List<String> findDistinctCategories();
}
