package org.example.backend.domain.likes.model.entity;

import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.user.model.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Likes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="user_idx")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="product_board_idx")
	private ProductBoard productBoard;
}
