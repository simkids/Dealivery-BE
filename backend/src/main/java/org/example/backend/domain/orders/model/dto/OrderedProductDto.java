package org.example.backend.domain.orders.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.orders.model.entity.OrderedProduct;
import org.example.backend.domain.orders.model.entity.Orders;

public class OrderedProductDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "주문 상품 데이터")
    public static class Request {
        @Schema(description = "게시글에 등록된 상품 idx", example = "1")
        private Long idx;
        @Schema(description = "주문한 수량", example = "3")
        private Integer quantity;
        public static OrderedProduct toEntity(Request request, Orders order) {
            return OrderedProduct.builder()
                    .productIdx(request.getIdx())
                    .quantity(request.quantity)
                    .orders(order)
                    .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderedProductResponse {
        private String name;
        private Integer amount;
        private Long price;
    }
}
