package org.example.backend.domain.orders.validator;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.orders.model.dto.OrderDto;
import org.example.backend.domain.orders.service.OrderQueueService;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.example.backend.global.common.constants.BaseResponseStatus.*;
import static org.example.backend.global.common.constants.BaseResponseStatus.ORDER_CREATE_FAIL_LACK_STOCK;

@Component
@RequiredArgsConstructor
public class OrdersValidator {
    private final ProductBoardRepository productBoardRepository;
    private final OrderQueueService orderQueueService;
    private final ProductRepository productRepository;
    public void validateOrder(OrderDto.OrderRegisterRequest order, Long userIdx, LocalDateTime registerdAt){

        ProductBoard board = productBoardRepository.findById(order.getBoardIdx())
                .orElseThrow(() -> {
                    orderQueueService.exitQueue(order.getBoardIdx(), userIdx);
                    return new InvalidCustomException(ORDER_FAIL_EVENT_NOT_FOUND);
                });

        if (board.getStartedAt().isAfter(registerdAt)) {
            throw new InvalidCustomException(ORDER_FAIL_UNOPENED_EVENT); // 시작하지 않은 이벤트일 때
        }

        if (board.getEndedAt().isBefore(registerdAt)) {
            throw new InvalidCustomException(ORDER_FAIL_EXPIRED_EVENT); // 이벤트가 끝났을 때
        }


        order.getOrderedProducts().forEach((product) -> {
            Product orderdProduct = productRepository.findByIdWithLock(product.getIdx())
                    .orElseThrow(() -> {
                        orderQueueService.exitQueue(order.getBoardIdx(), userIdx);
                        return new InvalidCustomException(ORDER_FAIL_PRODUCT_NOT_FOUND); // 해당하는 상품을 찾을 수가 없을 때
                    });

            if (product.getQuantity() > orderdProduct.getStock()) {
                orderQueueService.exitQueue(order.getBoardIdx(), userIdx);
                throw new InvalidCustomException(ORDER_CREATE_FAIL_LACK_STOCK); // 재고 수량 없을 때
            }
        });
    }
}
