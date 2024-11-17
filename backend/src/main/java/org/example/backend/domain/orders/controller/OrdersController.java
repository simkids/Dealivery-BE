package org.example.backend.domain.orders.controller;

import static org.example.backend.domain.orders.model.dto.OrderDto.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.orders.service.OrderService;

import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.SwaggerDescription;
import org.example.backend.global.common.constants.SwaggerExamples;

import org.example.backend.global.security.custom.model.dto.CustomCompanyDetails;
import org.example.backend.global.security.custom.model.dto.CustomUserDetails;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {
    private final OrderService orderService;

    @PostMapping("/register")
    @Operation(summary = "주문 등록 API", description = SwaggerDescription.ORDERS_REGISTER_REQUEST,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderRegisterRequest.class),
                            examples = @ExampleObject(value = SwaggerExamples.ORDERS_REGISTER_REQUEST)
                    )
            )
    )
    public ResponseEntity<BaseResponse> registerOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody OrderRegisterRequest order) {
        User user = customUserDetails.getUser();
        OrderCreateResponse res = orderService.register(user, order);
        log.info("[SUCCESS] Order register: [POST][200][Order ID : {}]", res.getOrderIdx());
        return ResponseEntity.ok(new BaseResponse(res));
    }

    @PostMapping("/complete")
    public ResponseEntity<BaseResponse> completeOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody OrderCompleteRequest order) {
        User user = customUserDetails.getUser();
        orderService.complete(user, order);
        log.info("[SUCCESS] Order complete: [POST][200][Order ID : {}]", order.getOrderIdx());
        return ResponseEntity.ok(new BaseResponse());
    }

    @PatchMapping("/{idx}/cancel")
    public ResponseEntity<BaseResponse> cancelOrder(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long idx) {
        User user = customUserDetails.getUser();
        orderService.cancel(user, idx);
        return ResponseEntity.ok(new BaseResponse());
    }

    @Operation(summary = "판매자 주문 내역 조회 API")
    @GetMapping(value = "/company/history")
    public BaseResponse companyOrderlist(@AuthenticationPrincipal CustomCompanyDetails customUserDetails, Integer page, String status, Integer month) {
        Company company = customUserDetails.getCompany();
        Page<CompanyOrderListResponse> boardListResponses = orderService.companyOrderList(company, page, status, month);
        return new BaseResponse(boardListResponses);
    }

    @Operation(summary = "판매자 주문 상세 조회 API")
    @GetMapping("/company/{idx}/detail")
    public BaseResponse companyOrderdetail(@AuthenticationPrincipal CustomCompanyDetails customUserDetails, @PathVariable Long idx){
        Company company = customUserDetails.getCompany();
        CompanyOrderDetailResponse res = orderService.companyOrderDetail(company, idx);
        return new BaseResponse(res);
    }

    @Operation(summary = "사용자 주문 내역 조회 API")
    @GetMapping(value = "/user/history")
    public BaseResponse userOrderlist(@AuthenticationPrincipal CustomUserDetails customUserDetails, Integer page, String status, Integer month) {
        User user = customUserDetails.getUser();
        Page<UserOrderListResponse> boardListResponses = orderService.userOrderList(user, page, status, month);
        return new BaseResponse(boardListResponses);
    }

    @Operation(summary = "사용자 주문 상세 조회 API")
    @GetMapping("/user/{idx}/detail")
    public BaseResponse userOrderdetail(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable Long idx){
        User user = customUserDetails.getUser();
        UserOrderDetailResponse res = orderService.userOrderDetail(user, idx);
        return new BaseResponse(res);
    }
}
