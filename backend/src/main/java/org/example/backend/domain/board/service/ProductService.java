package org.example.backend.domain.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.example.backend.domain.board.model.dto.ProductBoardDto;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
	private final ProductRepository productRepository;

	public List<Product> saveProduct(ProductBoardDto.BoardCreateRequest boardCreateRequest, ProductBoard productBoard) {
		return boardCreateRequest.getProducts().stream()
			.map(productDto -> productRepository.save(productDto.toEntity(productBoard)))
			.collect(Collectors.toList());
	}
}
