package com.adriantomczak.menumanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adriantomczak.menumanager.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}