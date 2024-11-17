package org.example.backend.domain.board.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.example.backend.domain.board.category.model.entity.Category;
import org.example.backend.domain.board.model.dto.ProductBoardDto;
import org.example.backend.domain.board.product.model.dto.ProductDto;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.likes.model.dto.LikesDto;
import org.example.backend.domain.likes.model.entity.Likes;
import org.example.backend.global.common.constants.BoardStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@EntityListeners(AuditingEntityListener.class)
public class ProductBoard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idx;
	private String title;
	@Column(length = 1000)
	private String productDetailUrl;
	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime modifiedAt;
	@Column(length = 1000)
	private String productThumbnailUrl;
	private String status;
	private Integer minimumPrice;
	private Integer discountRate;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productBoard")
	private List<ProductThumbnailImage> productThumbnailImages = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productBoard")
	private List<Product> products = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productBoard")
	private List<Likes> likes = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_idx")
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_idx")
	private Category category;


	/* TODO
	likes 부분 일단 false로 처리하고 추후 로그인 되어 있는 사용자일 경우 조회해서 값 세팅하도록 변경
	* */
	public static ProductBoardDto.BoardListResponse toBoardListResponse(ProductBoard productBoard) {

		return ProductBoardDto.BoardListResponse.builder()
			.idx(productBoard.idx)
			.productThumbnailUrl(productBoard.productThumbnailUrl)
			.title(productBoard.title)
			.companyName(productBoard.company.getCompanyName())
			.startedAt(productBoard.startedAt.withSecond(0).withNano(0))
			.endedAt(productBoard.endedAt.withSecond(0).withNano(0))
			.price(productBoard.products.stream().min(Comparator.comparing(Product::getPrice)).map(Product::getPrice).orElse(null))
			.discountRate(productBoard.discountRate)
			.likes(false)
			.build();
	}

	public static ProductBoardDto.BoardListResponse toBoardListResponse(ProductBoard productBoard, Boolean isLiked) {
		return ProductBoardDto.BoardListResponse.builder()
			.idx(productBoard.idx)
			.productThumbnailUrl(productBoard.productThumbnailUrl)
			.title(productBoard.title)
			.companyName(productBoard.company.getCompanyName())
			.startedAt(productBoard.startedAt.withSecond(0).withNano(0))
			.endedAt(productBoard.endedAt.withSecond(0).withNano(0))
			.price(productBoard.products.stream().min(Comparator.comparing(Product::getPrice)).map(Product::getPrice).orElse(null))
			.discountRate(productBoard.discountRate)
			.likes(isLiked)
			.build();
	}

	public static ProductBoardDto.BoardDetailResponse toBoardDetailResponse(ProductBoard productBoard, List<String> productThumbnailUrls, List<ProductDto.Response> products) {
		return ProductBoardDto.BoardDetailResponse.builder()
			.productThumbnailUrls(productThumbnailUrls)
			.productDetailUrl(productBoard.productDetailUrl)
			.title(productBoard.title)
			.products(products)
			.startedAt(productBoard.startedAt.withSecond(0).withNano(0))
			.endedAt(productBoard.endedAt.withNano(0).withNano(0))
			.companyName(productBoard.company.getCompanyName())
			.category(productBoard.category.getName())
			.likes(false)
			.price(products.stream().min(Comparator.comparing(ProductDto.Response::getPrice)).map(ProductDto.Response::getPrice).orElse(null))
			.discountRate(productBoard.discountRate)
			.build();
	}

	public ProductBoardDto.BoardDetailResponse toBoardDetailResponse(List<String> productThumbnailUrls, List<ProductDto.Response> products, Boolean isLiked) {
		return ProductBoardDto.BoardDetailResponse.builder()
			.productThumbnailUrls(productThumbnailUrls)
			.productDetailUrl(this.productDetailUrl)
			.title(this.title)
			.products(products)
			.startedAt(this.startedAt.withSecond(0).withNano(0))
			.endedAt(this.endedAt.withNano(0).withNano(0))
			.companyName(this.company.getCompanyName())
			.category(this.category.getName())
			.likes(isLiked)
			.price(products.stream().min(Comparator.comparing(ProductDto.Response::getPrice)).map(ProductDto.Response::getPrice).orElse(null))
			.discountRate(this.discountRate)
			.build();
	}


	// 판매자 게시글 목록 조회 DTO
	public static ProductBoardDto.CompanyBoardListResponse toCompanyBoardListResponse(ProductBoard productBoard) {
		return ProductBoardDto.CompanyBoardListResponse.builder()
			.idx(productBoard.idx)
			.productThumbnailUrl(productBoard.productThumbnailUrl)
			.title(productBoard.title)
			.category(productBoard.category.getName())
			.status(productBoard.status) // <- 이부분 수정해야됨
			.startedAt(productBoard.startedAt)
			.endedAt(productBoard.endedAt)
			.build();
	}

	public ProductBoardDto.CompanyBoardDetailResponse toCompanyBoardDetailResponse(List<String> productDetailUrls, List<ProductDto.CompanyResponse> products) {
		return ProductBoardDto.CompanyBoardDetailResponse.builder()
			.productThumbnailUrls(productDetailUrls)
			.productDetailUrl(this.productDetailUrl)
			.title(this.title)
			.discountRate(this.discountRate)
			.products(products)
			.startedAt(this.startedAt.withSecond(0).withNano(0))
			.endedAt(this.endedAt.withSecond(0).withNano(0))
			.category(this.category.getName())
			.build();
	}

	public LikesDto.LikeResponse toLikeResponse() {
		return LikesDto.LikeResponse.builder()
			.idx(this.idx)
			.productThumbnailUrl(this.productThumbnailUrl)
			.title(this.title)
			.startedAt(this.startedAt)
			.endedAt(this.endedAt)
			.companyName(this.company.getCompanyName())
			.category(this.category.getName())
			.price(this.minimumPrice)
			.discountRate(this.discountRate)
			.build();
	}
}
