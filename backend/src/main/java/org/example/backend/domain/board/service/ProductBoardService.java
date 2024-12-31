package org.example.backend.domain.board.service;

import java.util.List;

import org.example.backend.domain.board.category.model.entity.Category;
import org.example.backend.domain.board.category.repository.CategoryRepository;
import org.example.backend.domain.board.model.dto.ProductBoardDto;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.model.entity.ProductThumbnailImage;
import org.example.backend.domain.board.product.model.dto.ProductDto;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.likes.repository.LikesRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.common.constants.BoardStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.example.backend.global.infra.s3.uploader.ImageUploader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProductBoardService {
	private final ProductService productService;
	private final ProductThumbnailImageService productThumbnailImageService;
	private final ProductBoardQueueService productBoardQueueService;
	private final ProductBoardRepository productBoardRepository;
	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final LikesRepository likesRepository;
	private final ImageUploader imageUploader;

	public Slice<ProductBoardDto.BoardListResponse> mainList(String status, Pageable pageable) {
		Slice<ProductBoard> productBoards = productBoardRepository.searchByStatus(BoardStatus.from(status).getStatus(), pageable);
		return productBoards.map(ProductBoard::toBoardListResponse);
	}

	public Slice<ProductBoardDto.BoardListResponse> mainList(Long userIdx, String status, Pageable pageable) {
		Slice<ProductBoard> productBoards = productBoardRepository.searchByStatus(BoardStatus.from(status).getStatus(), pageable);
		return productBoards.map(productBoard -> {
			boolean isLiked = likesRepository.existsByProductBoardIdxAndUserIdx(productBoard.getIdx(), userIdx);
			return ProductBoard.toBoardListResponse(productBoard, isLiked);
		});
	}

	public Page<ProductBoardDto.BoardListResponse> list(String search, Pageable pageable) {
		Page<ProductBoard> productBoards = productBoardRepository.search(search, pageable);
		return productBoards.map(ProductBoard::toBoardListResponse);
	}

	public Page<ProductBoardDto.BoardListResponse> list(Long userIdx, String search, Pageable pageable) {
		Page<ProductBoard> productBoards = productBoardRepository.search(search, pageable);
		return productBoards.map(productBoard -> {
			boolean isLiked = likesRepository.existsByProductBoardIdxAndUserIdx(productBoard.getIdx(), userIdx);
			return ProductBoard.toBoardListResponse(productBoard, isLiked);
		});
	}

	public ProductBoardDto.BoardDetailResponse detail(Long idx) {
		ProductBoard productBoard = productBoardRepository.findByIdx(idx).orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_DETAIL_FAIL));
		List<ProductThumbnailImage> productThumbnailImages = productBoard.getProductThumbnailImages();
		List<Product> products = productRepository.findAllByProductBoardIdx(idx).orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_DETAIL_FAIL));


		List<String> productThumbnailUrls = productThumbnailImages.stream()
			.map(ProductThumbnailImage::getProductThumbnailUrl)
			.toList();
		List<ProductDto.Response> productResponse = products.stream()
			.map(Product::toResponse)
			.toList();
		return ProductBoard.toBoardDetailResponse(productBoard, productThumbnailUrls, productResponse);
	}

	public ProductBoardDto.BoardDetailResponse detail(Long userIdx, Long idx) {
		ProductBoard productBoard = productBoardRepository.findByIdx(idx).orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_DETAIL_FAIL));
		List<ProductThumbnailImage> productThumbnailImages = productBoard.getProductThumbnailImages();
		List<Product> products = productRepository.findAllByProductBoardIdx(idx).orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_DETAIL_FAIL));


		List<String> productThumbnailUrls = productThumbnailImages.stream()
			.map(ProductThumbnailImage::getProductThumbnailUrl)
			.toList();
		List<ProductDto.Response> productResponse = products.stream()
			.map(Product::toResponse)
			.toList();
		boolean isLiked = likesRepository.existsByProductBoardIdxAndUserIdx(productBoard.getIdx(), userIdx);
		return productBoard.toBoardDetailResponse(productThumbnailUrls, productResponse, isLiked);
	}

	@Transactional
	public ProductBoard create(Long companyIdx, ProductBoardDto.BoardCreateRequest boardCreateRequest, MultipartFile[] productThumbnails, MultipartFile productDetail) {
		List<String> thumbnailUrls = imageUploader.uploadImages(productThumbnails);
		String productDetailUrl = imageUploader.uploadImage(productDetail);

		ProductBoard savedProductBoard = saveProductBoard(companyIdx, boardCreateRequest, thumbnailUrls.get(0), productDetailUrl);
		List<Product> savedProducts = productService.saveProduct(boardCreateRequest, savedProductBoard);
		List<ProductThumbnailImage> productThumbnailImages = productThumbnailImageService.saveProductThumbnailImage(boardCreateRequest, thumbnailUrls, savedProductBoard);

		Boolean isCreated = productBoardQueueService.createQueue(savedProductBoard.getIdx(), savedProductBoard.getEndedAt());
		if (!isCreated) {
			throw new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_QUEUE_CREATE_FAIL);
		}
		return savedProductBoard;
	}

	// 판매자 게시글 조회
	public Page<ProductBoardDto.CompanyBoardListResponse> companyList(Long companyIdx, String status, Integer month, Pageable pageable) {
		Page<ProductBoard> productBoards = productBoardRepository.companySearch(companyIdx, status, month, pageable);
		return productBoards.map(ProductBoard::toCompanyBoardListResponse);
	}

	public ProductBoardDto.CompanyBoardDetailResponse getCompanyDetail(Long companyIdx, Long idx) {
		ProductBoard productBoard = productBoardRepository.findByCompanyIdxAndIdx(companyIdx, idx).orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_DETAIL_FAIL));
		List<ProductThumbnailImage> productThumbnailImages = productBoard.getProductThumbnailImages();
		List<Product> products = productRepository.findAllByProductBoardIdx(idx).orElseThrow(() -> new InvalidCustomException(BaseResponseStatus.PRODUCT_BOARD_DETAIL_FAIL));

		List<String> productThumbnailUrls = productThumbnailImages.stream()
			.map(ProductThumbnailImage::getProductThumbnailUrl)
			.toList();
		List<ProductDto.CompanyResponse> productCompanyResponse = products.stream()
			.map(Product::toCompanyResponse)
			.toList();
		return productBoard.toCompanyBoardDetailResponse(productThumbnailUrls, productCompanyResponse);
	}

	private ProductBoard saveProductBoard(Long companyIdx, ProductBoardDto.BoardCreateRequest boardCreateRequest, String productThumbnailUrl, String productDetailUrl) {
		Category category = categoryRepository.findByName(boardCreateRequest.getCategory().getType());
		ProductBoard productBoard = boardCreateRequest.toEntity(companyIdx, productThumbnailUrl, productDetailUrl, category);
		return productBoardRepository.save(productBoard);
	}
}
