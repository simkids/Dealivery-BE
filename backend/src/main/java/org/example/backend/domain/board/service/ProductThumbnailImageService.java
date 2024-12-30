package org.example.backend.domain.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.backend.domain.board.model.dto.ProductBoardDto;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.model.entity.ProductThumbnailImage;
import org.example.backend.domain.board.repository.ProductThumbnailImageRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductThumbnailImageService {
	private final ProductThumbnailImageRepository productThumbnailImageRepository;

	public List<ProductThumbnailImage> saveProductThumbnailImage(ProductBoardDto.BoardCreateRequest boardCreateRequest, List<String> thumbnailUrls, ProductBoard productBoard) {
		return thumbnailUrls.stream()
			.map(url -> productThumbnailImageRepository.save(boardCreateRequest.toEntity(url, productBoard)))
			.collect(Collectors.toList());
	}
}
