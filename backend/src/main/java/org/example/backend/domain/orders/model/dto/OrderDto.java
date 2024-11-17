package org.example.backend.domain.orders.model.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

import lombok.Getter;
import org.example.backend.domain.orders.model.dto.OrderedProductDto.OrderedProductResponse;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.OrderStatus;
import org.example.backend.global.common.constants.PaymentType;
import org.example.backend.global.utils.RandomCodeGenerator;

public class OrderDto {
    @Builder
    @Getter
    public static class OrderRegisterRequest {
        @Schema(description = "게시글 idx")
        private Long boardIdx;

        @ArraySchema(arraySchema = @Schema(description = "주문한 상품들"), schema = @Schema(implementation = OrderedProductDto.Request.class))
        private List<OrderedProductDto.Request> orderedProducts;
        public static Orders toEntity(Long boardIdx, User user) {
            return Orders.builder()
                    .boardIdx(boardIdx)
                    .user(user)
                    .ordersNumber(RandomCodeGenerator.generate(10))
                    .status(OrderStatus.PAYMENT_WAIT)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class OrderCompleteRequest {
        private Long orderIdx;

        private String receiverName;
        private String receiverPhoneNumber;

        private String address;
        private String addressDetail;
        private String postNumber;

        private String paymentId;
        private Long totalPaidAmount;
        private Long originalPaidAmount;
        private PaymentType payMethod;
        private Long usedPoint;
    }

    @Builder
    @Getter
    public static class OrderCreateResponse {
        private Long orderIdx;
    }

    @Builder
    @Getter
    public static class CompanyOrderListResponse {
        private Long orderIdx;
        private String ordersNumber;
        private String payMethod;
        private String title;
        private String ordererName;
        private Long totalPaidAmount;
        private String status;
        private LocalDateTime modifiedAt;
    }

    @Builder
    @Getter
    public static class CompanyOrderDetailResponse {
        private Long orderIdx;
        private List<OrderedProductResponse> products;
    }

    @Builder
    @Getter
    public static class UserOrderListResponse {
        private Long orderIdx;
        private String ordersNumber;
        private String status;

        private LocalDateTime createdAt;

        private String title;
        private String thumnailUrl;
        private Integer minimumPrice;
        private Integer discountRate;
    }

    @Builder
    @Getter
    public static class UserOrderDetailResponse {
        private String ordersNumber;
        private String status;
        private LocalDateTime createdAt;

        private Long totalPaidAmount;
        private Long originalPaidAmount;
        private String payMethod;
        private Long usedPoint;


        private String receiverName;
        private String receiverPhoneNumber;

        private String address;

    }
}
