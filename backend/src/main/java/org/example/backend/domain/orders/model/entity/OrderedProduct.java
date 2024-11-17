package org.example.backend.domain.orders.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.orders.model.dto.OrderDto.CompanyOrderDetailResponse;
import org.example.backend.domain.orders.model.dto.OrderDto.UserOrderListResponse;
import org.example.backend.domain.orders.model.dto.OrderedProductDto.OrderedProductResponse;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name="orders_idx")
    private Orders orders;

    private Long productIdx;

    public OrderedProductResponse toOrderedProductResponse(Product product, Integer discountRate) {
        return OrderedProductResponse.builder()
                .name(product.getName())
                .amount(quantity)
                .price(Math.round(product.getPrice() * quantity * (1 - discountRate / 100.0)))
                .build();
    }
}
