package org.example.backend.domain.board.repository;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.model.entity.ProductThumbnailImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductThumbnailImageRepository extends JpaRepository<ProductThumbnailImage, Long> {
	// @Query("select pti from ProductThumbnailImage pti where pti.productBoard.idx = :idx")
	Optional<List<ProductThumbnailImage>> findAllByProductBoardIdx(Long idx);
}
