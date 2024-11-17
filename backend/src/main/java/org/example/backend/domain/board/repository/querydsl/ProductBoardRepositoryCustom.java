package org.example.backend.domain.board.repository.querydsl;

import org.example.backend.domain.board.model.entity.ProductBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ProductBoardRepositoryCustom {
	Page<ProductBoard> search(String search, Pageable pageable);
	Page<ProductBoard> companySearch(Long companyIdx, String status, Integer month, Pageable pageable);
	Slice<ProductBoard> searchByStatus(String status, Pageable pageable);
}
