package org.example.backend.domain.board.repository;

import java.util.List;
import java.util.Optional;

import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.repository.querydsl.ProductBoardRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductBoardRepository extends JpaRepository<ProductBoard, Long>, ProductBoardRepositoryCustom {
	@Query("SELECT pb FROM ProductBoard pb JOIN FETCH pb.category JOIN FETCH pb.company JOIN FETCH pb.productThumbnailImages WHERE pb.idx = :idx")
	Optional<ProductBoard> findByIdx(Long idx);

	List<ProductBoard> findByCompanyEmail(String companyEmail);

	@Query("SELECT pb FROM ProductBoard pb JOIN FETCH pb.category JOIN FETCH pb.productThumbnailImages WHERE pb.idx = :idx and pb.company.idx = :companyIdx")
	Optional<ProductBoard> findByCompanyIdxAndIdx(Long companyIdx, Long idx);
}
