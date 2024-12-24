package org.example.backend.domain.orders.service;

import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.orders.model.dto.OrderDto;
import org.example.backend.domain.orders.model.dto.OrderedProductDto;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.orders.repository.OrderedProductRepository;
import org.example.backend.domain.orders.repository.OrdersRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.orders.validator.OrdersValidator;
import org.example.backend.domain.user.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrdersValidator ordersValidator;

    @Mock
    private OrderQueueService orderQueueService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderedProductRepository orderedProductRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductBoardRepository productBoardRepository;


    @DisplayName("[해피 케이스] 회원 정보와 요청 파라미터를 넘겨받아 주문을 생성한다.")
    @Test
    void register() {
        // given
        User user = User.builder().idx(1L).build();
        Long boardIdx = 1L;
        List<OrderedProductDto.Request> orderedProducts = List.of(
                createOrderdProductDto(1,1), createOrderdProductDto(2,2)
        );
        OrderDto.OrderRegisterRequest request = OrderDto.OrderRegisterRequest.builder()
                .boardIdx(boardIdx)
                .orderedProducts(orderedProducts)
                .build();

        Orders order = Orders.builder().idx(1L).build();


        willDoNothing().given(ordersValidator).validateOrder(any(OrderDto.OrderRegisterRequest.class),any(Long.class), any(LocalDateTime.class));
        given(ordersRepository.save(any(Orders.class))).willReturn(order);

        // when
        OrderDto.OrderCreateResponse response = orderService.register(user, request);

        // then
        assertThat(response.getOrderIdx()).isEqualTo(1L);
    }

    private static OrderedProductDto.Request createOrderdProductDto(long idx, int quantity) {
        return OrderedProductDto.Request.builder()
                .idx(idx)
                .quantity(quantity)
                .build();
    }
}