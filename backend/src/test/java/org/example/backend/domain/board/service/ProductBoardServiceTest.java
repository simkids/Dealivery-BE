package org.example.backend.domain.board.service;

import org.example.backend.domain.board.category.model.entity.Category;
import org.example.backend.domain.board.category.repository.CategoryRepository;
import org.example.backend.domain.board.model.dto.ProductBoardDto;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.model.entity.ProductThumbnailImage;
import org.example.backend.domain.board.product.model.dto.ProductDto;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.global.common.constants.CategoryType;
import org.example.backend.global.infra.s3.uploader.ImageUploader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductBoardServiceTest {
	@Mock
	private ProductService productService;
	@Mock
	private ProductThumbnailImageService productThumbnailImageService;
	@Mock
	private ProductBoardQueueService productBoardQueueService;
	@Mock
	private ProductBoardRepository productBoardRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ImageUploader imageUploader;
	@InjectMocks
	private ProductBoardService productBoardService;

	@DisplayName("[해피 케이스] 회원 정보와 요청 파라미터를 넘겨받아 주문을 생성한다.")
	@Test
	void createBoard() {
		// given
		List<ProductDto.Request> productDtoRequests = List.of(createProductRequest(1000), createProductRequest(2000));
		ProductBoardDto.BoardCreateRequest productBoardRequest = createBoardRequest(productDtoRequests, CategoryType.CLOTHES);

		List<String> thumbnailUrls = List.of("thumbnailURL1", "thumbnailURL2");
		String detailUrl = "detailURL";
		Category category = createCategory(1L);
		ProductBoard productBoard = createProductBoard(thumbnailUrls.get(0), detailUrl, category);

		given(imageUploader.uploadImages(any()))
			.willReturn(thumbnailUrls);
		given(imageUploader.uploadImage(any()))
			.willReturn(detailUrl);
		given(categoryRepository.findByName(any(String.class)))
			.willReturn(category);
		given(productBoardRepository.save(any(ProductBoard.class)))
			.willReturn(productBoard);
		given(productService.saveProduct(any(), any()))
			.willReturn(List.of(createProduct(1L), createProduct(2L)));
		given(productThumbnailImageService.saveProductThumbnailImage(any(), any(), any()))
			.willReturn(
				List.of(createProductThumbnailImage(1L, "thumbnailURL1"),
					createProductThumbnailImage(2L, "thumbnailURL2"))
			);
		given(productBoardQueueService.createQueue(any(Long.class), any()))
			.willReturn(true);

		// when
		ProductBoard savedProductBoard = productBoardService.create(1L, productBoardRequest, new MultipartFile[]{}, new MockMultipartFile("detailURL", new byte[]{}));

		// then
		assertThat(savedProductBoard.getProductThumbnailUrl()).isEqualTo(thumbnailUrls.get(0));
		assertThat(savedProductBoard.getProductDetailUrl()).isEqualTo(detailUrl);
		assertThat(savedProductBoard.getCompany().getIdx()).isEqualTo(1L);
		assertThat(savedProductBoard.getCategory().getIdx()).isEqualTo(1L);
	}

	private ProductBoardDto.BoardCreateRequest createBoardRequest(List<ProductDto.Request> products, CategoryType category) {
		return ProductBoardDto.BoardCreateRequest.builder()
			.products(products)
			.category(category)
			.build();
	}

	private ProductDto.Request createProductRequest(int price) {
		return ProductDto.Request.builder().price(price).build();
	}

	private Category createCategory(Long idx) {
		return Category.builder().idx(idx).build();
	}

	private ProductBoard createProductBoard(String productThumbnailUrl, String detailUrl, Category category) {
		return ProductBoard.builder()
			.idx(1L)
			.company(Company.builder().idx(1L).build())
			.productThumbnailUrl(productThumbnailUrl)
			.productDetailUrl(detailUrl)
			.category(category)
			.build();
	}

	private Product createProduct(Long idx) {
		return Product.builder().idx(idx).build();
	}

	private ProductThumbnailImage createProductThumbnailImage(Long idx, String productThumbnailUrl) {
		return ProductThumbnailImage.builder().idx(idx).productThumbnailUrl(productThumbnailUrl).build();
	}
}
