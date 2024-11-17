package org.example.backend.domain.board.controller;

import org.example.backend.domain.board.model.dto.ProductBoardDto;
import org.example.backend.domain.board.service.ProductBoardService;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.common.constants.BoardStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.example.backend.global.security.custom.model.dto.CustomCompanyDetails;
import org.example.backend.global.security.custom.model.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-boards")
public class BoardController {
	private final ProductBoardService productBoardService;
	private final Integer MAIN_OPEN = 8;
	private final Integer MAIN_READY = 12;
	private final Integer USER_LIST_SIZE = 21;
	private final Integer COMPANY_LIST_SIZE = 10;

	@Operation(summary = "상품 메인 목록 조회 API")
	@GetMapping(value = "/main/list")
	public BaseResponse mainList(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value = "page", defaultValue = "1") Integer page,
		@RequestParam(value = "status", defaultValue = "진행 전") String status) {
		if (!isValidStatus(status)) {
			return new BaseResponse(BaseResponseStatus.FAIL);
		}
		Slice<ProductBoardDto.BoardListResponse> responses;
		Pageable pageable = getPageableByStatus(status, page);
		if (customUserDetails == null) {
			responses = productBoardService.mainList(status, pageable);
		 }
		else {
			responses = productBoardService.mainList(customUserDetails.getIdx(), status, pageable);
		}
		return new BaseResponse(responses);
	}

	@Operation(summary = "상품 게시글 목록 조회 API")
	@GetMapping(value = "/list")
	public BaseResponse list(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam(value = "page", defaultValue = "1")Integer page, @RequestParam(required = false) String search) {
		Pageable pageable = PageRequest.of(page - 1, USER_LIST_SIZE);
		Page<ProductBoardDto.BoardListResponse> boardListResponses;
		if (customUserDetails == null) {
			boardListResponses = productBoardService.list(search, pageable);
		} else {
			boardListResponses = productBoardService.list(customUserDetails.getIdx(), search, pageable);
		}
		return new BaseResponse(boardListResponses);
	}

	@Operation(summary = "상품 게시글 상세 조회 API")
	@GetMapping(value = "/{idx}/detail")
	public BaseResponse detail(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long idx) {
		ProductBoardDto.BoardDetailResponse response;
		if (customUserDetails == null) {
			response = productBoardService.detail(idx);
		} else {
			response = productBoardService.detail(customUserDetails.getIdx(), idx);
		}
		return response == null ? new BaseResponse(BaseResponseStatus.FAIL) : new BaseResponse(response);
	}

	@Operation(summary = "판매자 회원 상품 등록 API")
	@PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE , MediaType.MULTIPART_FORM_DATA_VALUE})
	public BaseResponse create(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails, @Valid @RequestPart("boardCreateRequest") ProductBoardDto.BoardCreateRequest boardCreateRequest,
		@RequestPart(value = "productThumbnails") MultipartFile[] productThumbnails,
		@RequestPart(value = "productDetail") MultipartFile productDetail) {
		if (customCompanyDetails == null) {
			throw new InvalidCustomException(BaseResponseStatus.FAIL);
		}
		productBoardService.create(customCompanyDetails.getIdx(), boardCreateRequest, productThumbnails, productDetail);
		return new BaseResponse();
	}

	@Operation(summary = "판매자 회원 게시글 조회 API")
	@GetMapping(value = "/company/list")
	public BaseResponse companyList(
		@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails, @RequestParam(value = "page", defaultValue = "1")Integer page,
		@RequestParam(required = false) String status, @RequestParam(required = false) Integer month) {
		if (customCompanyDetails == null) {
			throw new InvalidCustomException(BaseResponseStatus.FAIL);
		}
		Pageable pageable = PageRequest.of(page - 1, COMPANY_LIST_SIZE);
		BoardStatus boardStatus = status == null ? null : BoardStatus.from(status);
		Page<ProductBoardDto.CompanyBoardListResponse> boardListResponses = productBoardService.companyList(customCompanyDetails.getIdx(), boardStatus == null ? null : boardStatus.getStatus(), month, pageable);
		return new BaseResponse(boardListResponses);
	}

	@Operation(summary = "판매자 회원 게시글 상세 조회 API")
	@GetMapping(value = "/company/{idx}/detail")
	public BaseResponse companyDetail(@AuthenticationPrincipal CustomCompanyDetails customCompanyDetails, @PathVariable Long idx) {
		if (customCompanyDetails == null) {
			throw new InvalidCustomException(BaseResponseStatus.FAIL);
		}
		ProductBoardDto.CompanyBoardDetailResponse response =  productBoardService.getCompanyDetail(customCompanyDetails.getIdx(), idx);
		return response == null ? new BaseResponse(BaseResponseStatus.FAIL) : new BaseResponse(response);
	}

	private boolean isValidStatus(String status) {
		return status.equals(BoardStatus.READY.getStatus()) || status.equals(BoardStatus.OPEN.getStatus());
	}

	private Pageable getPageableByStatus(String status, Integer page) {
		int pageSize = getPageSizeByStatus(status);
		return PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "idx"));
	}

	private int getPageSizeByStatus(String status) {
		if (status.equals(BoardStatus.READY.getStatus())) {
			return MAIN_READY;
		} else {
			return MAIN_OPEN;
		}
	}
}
