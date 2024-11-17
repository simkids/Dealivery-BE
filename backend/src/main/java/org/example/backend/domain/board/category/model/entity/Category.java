package org.example.backend.domain.board.category.model.entity;

import java.util.List;

import org.example.backend.domain.board.model.entity.ProductBoard;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;

	@Column(unique = true)
	private String name;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
	private List<ProductBoard> productBoards;

}
