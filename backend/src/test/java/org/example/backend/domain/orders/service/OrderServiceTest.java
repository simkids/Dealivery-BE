package org.example.backend.domain.orders.service;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.orders.model.dto.OrderDto;
import org.example.backend.domain.orders.model.dto.OrderedProductDto;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.orders.repository.OrderedProductRepository;
import org.example.backend.domain.orders.repository.OrdersRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.orders.validator.OrdersValidator;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.common.constants.OrderStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import retrofit2.HttpException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.backend.global.common.constants.BaseResponseStatus.ORDER_PAYMENT_FAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private PaymentService paymentService;

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


    @DisplayName("[해피 케이스] 결제 성공 시 주문 상태가 주문 완료로 업데이트된다.")
    @Test
    void completeWithValidPayment() throws IamportResponseException, IOException {
        // given
        User user = User.builder()
                .idx(1L)
                .point(10000L)
                .build();

        Orders order = Orders.builder().idx(1L).user(user).status(OrderStatus.PAYMENT_WAIT).build();

        String validPaymentId = "validPaymentId";
        OrderDto.OrderCompleteRequest request = OrderDto.OrderCompleteRequest.builder()
                .orderIdx(order.getIdx())
                .paymentId(validPaymentId)
                .usedPoint(100L)
                .totalPaidAmount(50000L)
                .build();

        given(ordersRepository.findById(any(Long.class))).willReturn(Optional.of(order));
        given(paymentService.getPaymentInfo(eq(validPaymentId))).willReturn(new Payment());
        willDoNothing().given(paymentService).validatePayment(any(Payment.class), eq(order));

        // when
        orderService.complete(user, request);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER_COMPLETE);
    }


    @DisplayName("[예외 케이스] 결제 실패 시, 주문 상태가 주문 실패로 업데이트된다.")
    @Test
    void completeWithFailedPayment() throws IamportResponseException, IOException {
        // given
        User user = User.builder()
                .idx(1L)
                .point(10000L)
                .build();

        Orders order = Orders.builder().idx(1L).user(user).status(OrderStatus.PAYMENT_WAIT).build();

        String invalidPaymentId = "invalidPaymentId";
        OrderDto.OrderCompleteRequest request = OrderDto.OrderCompleteRequest.builder()
                .orderIdx(order.getIdx())
                .paymentId(invalidPaymentId)
                .usedPoint(100L)
                .totalPaidAmount(50000L)
                .build();


        HttpException httpException = new HttpException(retrofit2.Response.error(404, okhttp3.ResponseBody.create(null, "")));
        IamportResponseException iamportException = new IamportResponseException("Payment not found", httpException);


        given(ordersRepository.findById(any(Long.class))).willReturn(Optional.of(order));
        given(paymentService.getPaymentInfo(eq(invalidPaymentId))).willThrow(iamportException);

        // when
        assertThatThrownBy(() -> orderService.complete(user, request))
                .isInstanceOf(InvalidCustomException.class)
                .hasMessage(BaseResponseStatus.ORDER_PAYMENT_FAIL.getMessage());

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ORDER_FAIL);
    }



    @DisplayName("[예외 케이스] 주문 정보를 업데이트 할 때, 존재하지 않는 주문일 경우, 예외가 발생한다.")
    @Test
    void completeWithNullEvent() {
        // given
        User user = User.builder().idx(1L).build();
        OrderDto.OrderCompleteRequest request = OrderDto.OrderCompleteRequest.builder()
                .orderIdx(1L)
                .build();

        given(ordersRepository.findById(request.getOrderIdx())).willReturn(Optional.empty());

        // when // then
        assertThatThrownBy(() -> orderService.complete(user, request))
                .isInstanceOf((InvalidCustomException.class))
                .hasMessage(BaseResponseStatus.ORDER_FAIL_NOT_FOUND.getMessage());
    }


    @DisplayName("[예외 케이스] 주문 정보 업데이트 중, 주문자가 일치하지 않을 경우 예외가 발생한다.")
    @Test
    void completeWithNotMatchedUser() {
        // given
        User user = User.builder().idx(1L).build();
        User anotherUser = User.builder().idx(2L).build();

        Orders order = Orders.builder().idx(1L).user(anotherUser).build();
        OrderDto.OrderCompleteRequest request = OrderDto.OrderCompleteRequest.builder()
                .orderIdx(order.getIdx())
                .build();

        given(ordersRepository.findById(request.getOrderIdx())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.complete(user, request))
                .isInstanceOf(InvalidCustomException.class)
                .hasMessage(BaseResponseStatus.ORDER_PAYMENT_FAIL.getMessage());
    }

    @DisplayName("[예외 케이스] 주문 취소 요청 중, 존재하지 않는 주문일 경우 예외가 발생한다.")
    @Test
    void cancelWithNotFoundOrder() {
        // given
        User user = User.builder().idx(1L).build();

        given(ordersRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.cancel(user, 1L))
                .isInstanceOf(InvalidCustomException.class)
                .hasMessage(BaseResponseStatus.ORDER_FAIL_NOT_FOUND.getMessage());
    }

    @DisplayName("[예외 케이스] 주문 취소 요청 중, 사용자가 일치하지 않을 경우 예외가 발생한다.")
    @Test
    void cancelWithNotMatchedUser() {
        // given
        User user = User.builder().idx(1L).build();
        User anotherUser = User.builder().idx(2L).build();

        Orders order = Orders.builder()
                .idx(1L)
                .user(anotherUser)
                .status(OrderStatus.ORDER_COMPLETE)
                .build();

        given(ordersRepository.findById(order.getIdx())).willReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> orderService.cancel(user, order.getIdx()))
                .isInstanceOf(InvalidCustomException.class)
                .hasMessage(BaseResponseStatus.ORDER_CANCEL_FAIL.getMessage());
    }


    private static OrderedProductDto.Request createOrderdProductDto(long idx, int quantity) {
        return OrderedProductDto.Request.builder()
                .idx(idx)
                .quantity(quantity)
                .build();
    }
}