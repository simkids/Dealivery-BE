package org.example.backend.domain.board.category.repository;

import org.example.backend.domain.board.category.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category findByName(String name);
}
