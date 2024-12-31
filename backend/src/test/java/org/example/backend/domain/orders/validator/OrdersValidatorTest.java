package org.example.backend.domain.orders.validator;

import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;

import org.example.backend.domain.orders.model.dto.OrderDto;
import org.example.backend.domain.orders.model.dto.OrderedProductDto;
import org.example.backend.domain.orders.service.OrderQueueService;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrdersValidatorTest {

    @InjectMocks
    private OrdersValidator ordersValidator;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductBoardRepository productBoardRepository;

    @Mock
    private OrderQueueService orderQueueService;

    @DisplayName("[해피 케이스] 주문 생성에 대한 유효성을 검증한다")
    @Test
    void validateOrder() {
        // given
        OrderedProductDto.Request req1 = createRequest(1L, 5);
        List<OrderedProductDto.Request> orderedProducts = List.of(req1);
        OrderDto.OrderRegisterRequest order = createOrderRequest(1L, orderedProducts);

        ProductBoard board = createBoard(1L,LocalDateTime.of(2024, 12, 23, 8, 30), LocalDateTime.of(2024, 12, 24, 8, 30));
        given(productBoardRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(board));
        Product product = createProduct(1L,5);
        given(productRepository.findByIdWithLock(any(Long.class))).willReturn(Optional.ofNullable(product));
        LocalDateTime registerdTime = LocalDateTime.of(2024,12,24,6,30);
        Long userIdx = 1L;

        // when // then (void 타입 -> 호출 여부 검증)
        ordersValidator.validateOrder(order, userIdx, registerdTime);
    }

    @DisplayName("[예외 케이스] 이벤트를 찾을 수 없을 때 예외가 발생한다.")
    @Test
    void validateOrderWithNullEvent() {
        // given
        OrderedProductDto.Request req1 = createRequest(1L, 5);
        List<OrderedProductDto.Request> orderedProducts = List.of(req1);
        OrderDto.OrderRegisterRequest order = createOrderRequest(1L, orderedProducts);

        LocalDateTime registerdTime = LocalDateTime.of(2024,12,24,8,29);
        Long userIdx = 1L;

        given(productBoardRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(null));

        // when // then (void 타입 -> 호출 여부 검증)
        assertThatThrownBy(() -> ordersValidator.validateOrder(order, userIdx, registerdTime))
                .isInstanceOf((InvalidCustomException.class))
                .hasMessage(BaseResponseStatus.ORDER_FAIL_EVENT_NOT_FOUND.getMessage());

    }

    @DisplayName("[예외 케이스] 만료된 이벤트에 주문을 요청할 때 예외가 발생한다.")
    @Test
    void validateOrderWithExpiredEvent() {
        // given
        OrderedProductDto.Request req1 = createRequest(1L, 5);
        List<OrderedProductDto.Request> orderedProducts = List.of(req1);
        OrderDto.OrderRegisterRequest order = createOrderRequest(1L, orderedProducts);

        ProductBoard board = createBoard(1L, LocalDateTime.of(2024, 12, 23, 8, 30), LocalDateTime.of(2024, 12, 24, 8, 30));

        LocalDateTime registerdTime = LocalDateTime.of(2024,12,24,8,31);
        Long userIdx = 1L;

        given(productBoardRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(board));

        // when // then (void 타입 -> 호출 여부 검증)
        assertThatThrownBy(() -> ordersValidator.validateOrder(order, userIdx, registerdTime))
                .isInstanceOf((InvalidCustomException.class))
                .hasMessage(BaseResponseStatus.ORDER_FAIL_EXPIRED_EVENT.getMessage());

    }

    @DisplayName("[예외 케이스] 시작 전인 이벤트에 주문을 요청할 때 예외가 발생한다.")
    @Test
    void validateOrderWithUnopenedEvent() {
        // given
        OrderedProductDto.Request req1 = createRequest(1L, 5);
        List<OrderedProductDto.Request> orderedProducts = List.of(req1);
        OrderDto.OrderRegisterRequest order = createOrderRequest(1L, orderedProducts);

        ProductBoard board = createBoard(1L, LocalDateTime.of(2024, 12, 24, 8, 30), LocalDateTime.of(2024, 12, 25, 8, 30));

        LocalDateTime registerdTime = LocalDateTime.of(2024,12,24,8,29);
        Long userIdx = 1L;

        given(productBoardRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(board));

        // when // then (void 타입 -> 호출 여부 검증)
        assertThatThrownBy(() -> ordersValidator.validateOrder(order, userIdx, registerdTime))
                .isInstanceOf((InvalidCustomException.class))
                .hasMessage(BaseResponseStatus.ORDER_FAIL_UNOPENED_EVENT.getMessage());

    }

    @DisplayName("[예외 케이스] 주문한 상품이 존재하지 않을 때 예외가 발생한다.")
    @Test
    void validateOrderWithNullProduct() {
        // given
        OrderedProductDto.Request req1 = createRequest(1L, 5);
        List<OrderedProductDto.Request> orderedProducts = List.of(req1);
        OrderDto.OrderRegisterRequest order = createOrderRequest(1L, orderedProducts);

        ProductBoard board = createBoard(1L, LocalDateTime.of(2024, 12, 23, 8, 30), LocalDateTime.of(2024, 12, 25, 8, 30));

        LocalDateTime registerdTime = LocalDateTime.of(2024,12,24,8,31);
        Long userIdx = 1L;

        given(productBoardRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(board));
        given(productRepository.findByIdWithLock(any(Long.class))).willReturn(Optional.ofNullable(null));

        // when // then (void 타입 -> 호출 여부 검증)
        assertThatThrownBy(() -> ordersValidator.validateOrder(order, userIdx, registerdTime))
                .isInstanceOf((InvalidCustomException.class))
                .hasMessage(BaseResponseStatus.ORDER_FAIL_PRODUCT_NOT_FOUND.getMessage());

    }

    @DisplayName("[예외 케이스] 주문한 상품의 재고가 부족하면 예외가 발생한다.")
    @Test
    void validateOrderWithOutOfStock() {
        // given
        OrderedProductDto.Request req1 = createRequest(1L, 2);
        List<OrderedProductDto.Request> orderedProducts = List.of(req1);
        OrderDto.OrderRegisterRequest order = createOrderRequest(1L, orderedProducts);

        ProductBoard board = createBoard(1L, LocalDateTime.of(2024, 12, 23, 8, 30), LocalDateTime.of(2024, 12, 24, 8, 30));
        given(productBoardRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(board));
        Product product = createProduct(1L, 1);
        given(productRepository.findByIdWithLock(any(Long.class))).willReturn(Optional.ofNullable(product));
        LocalDateTime registerdTime = LocalDateTime.of(2024, 12, 24, 6, 30);
        Long userIdx = 1L;

        // when // then (void 타입 -> 호출 여부 검증)
        assertThatThrownBy(() -> ordersValidator.validateOrder(order, userIdx, registerdTime))
                .isInstanceOf((InvalidCustomException.class))
                .hasMessage(BaseResponseStatus.ORDER_CREATE_FAIL_LACK_STOCK.getMessage());

    }

    private OrderedProductDto.Request createRequest(long idx, int quantity) {
        return OrderedProductDto.Request.builder()
                .idx(idx)
                .quantity(quantity)
                .build();
    }

    private OrderDto.OrderRegisterRequest createOrderRequest(Long idx, List<OrderedProductDto.Request> orderedProducts) {
        return OrderDto.OrderRegisterRequest.builder()
                .boardIdx(idx)
                .orderedProducts(orderedProducts)
                .build();
    }
    private ProductBoard createBoard(Long idx, LocalDateTime startedAt, LocalDateTime endedAt) {
        return ProductBoard.builder()
                .idx(idx)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
    }

    private Product createProduct(Long idx, Integer stock) {
        return Product.builder()
                .idx(idx)
                .stock(stock)
                .build();
    }
}