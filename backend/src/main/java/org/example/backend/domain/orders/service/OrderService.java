package org.example.backend.domain.orders.service;

import static org.example.backend.domain.orders.model.dto.OrderDto.*;
import static org.example.backend.global.common.constants.BaseResponseStatus.*;

import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.Payment;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.board.repository.ProductBoardRepository;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.orders.model.dto.OrderedProductDto;
import org.example.backend.domain.orders.model.dto.OrderedProductDto.OrderedProductResponse;
import org.example.backend.domain.orders.model.entity.OrderedProduct;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.domain.orders.repository.OrderedProductRepository;
import org.example.backend.domain.orders.repository.OrdersRepository;
import org.example.backend.domain.orders.validator.OrdersValidator;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.OrderStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final int COMPANY_PAGE_SIZE = 5;
    private final int USER_PAGE_SIZE = 3;
    private final PaymentService paymentService;
    private final OrderQueueService orderQueueService;

    private final OrdersRepository ordersRepository;
    private final OrderedProductRepository orderedProductRepository;
    private final ProductRepository productRepository;
    private final ProductBoardRepository productBoardRepository;

    private final OrdersValidator ordersValidator;

    @Transactional
    public OrderCreateResponse register(User user, OrderRegisterRequest request) {

        ordersValidator.validateOrder(request, user.getIdx(), LocalDateTime.now());

        Orders order = OrderRegisterRequest.toEntity(request.getBoardIdx(), user);
        Orders savedOrder = ordersRepository.save(order);

        List<OrderedProduct> orderedProducts = request.getOrderedProducts().stream()
                .map(product -> OrderedProductDto.Request.toEntity(product, savedOrder))
                .collect(Collectors.toList());

        orderedProductRepository.saveAll(orderedProducts);

        return OrderCreateResponse.builder()
                .orderIdx(savedOrder.getIdx())
                .build();
    }


    public void complete(User user, OrderCompleteRequest request) {

        Orders order = ordersRepository.findById(request.getOrderIdx()).orElseThrow(() -> new InvalidCustomException(
                ORDER_FAIL_NOT_FOUND));

        if (order.getUser().getIdx() != user.getIdx()) {
            log.info("[Faild] Order complete failed: user idx not same [db idx : {}, user idx : {}]", order.getUser().getIdx(),user.getIdx());
            throw new InvalidCustomException(ORDER_PAYMENT_FAIL);
        }

        order.update(request); // 주문 추가 정보 업데이트
        ordersRepository.save(order);

        String paymentId = request.getPaymentId();

        try {
            Payment payment = paymentService.getPaymentInfo(paymentId);
            paymentService.validatePayment(payment, order);
            order.getUser().deductPoints(order.getUsedPoint());
            order.setStatus(OrderStatus.ORDER_COMPLETE);
            ordersRepository.save(order);
            orderQueueService.exitQueue(order.getBoardIdx(), user.getIdx());

        } catch (IamportResponseException | IOException e) { // 해당하는 결제 정보를 찾지 못했을 때
            log.info("[Faild] Order complete failed: payment info not found");
            order.setStatus(OrderStatus.ORDER_FAIL);
            ordersRepository.save(order);
            orderQueueService.exitQueue(order.getBoardIdx(), user.getIdx());
            throw new InvalidCustomException(ORDER_PAYMENT_FAIL);

        } catch (InvalidCustomException e) { // 결제 검증 중 발생한 예외 처리
            order.setStatus(OrderStatus.ORDER_FAIL);
            ordersRepository.save(order);
            orderQueueService.exitQueue(order.getBoardIdx(), user.getIdx());
            throw e;
        }
    }

    @Transactional
    public void cancel(User user, Long idx) {
        Orders order = ordersRepository.findById(idx).orElseThrow(() -> new InvalidCustomException(
                ORDER_FAIL_NOT_FOUND));

        if (order.getUser().getIdx() != user.getIdx())  { // 해당하는 사용자의 주문이 아닐 때
            throw new InvalidCustomException(ORDER_CANCEL_FAIL);
        }

        if (order.getStatus() !=  OrderStatus.ORDER_COMPLETE) {
            ordersRepository.delete(order);
            orderQueueService.exitQueue(order.getBoardIdx(), user.getIdx());
            return;
        }

        ProductBoard board = productBoardRepository.findById(order.getBoardIdx()).orElseThrow(() -> new InvalidCustomException(ORDER_CANCEL_FAIL));

        if (order.getStatus() ==  OrderStatus.ORDER_COMPLETE && board.getEndedAt().isAfter(LocalDateTime.now())) {
            rollbackStock(order);

            String impUid = order.getPaymentId();
            try {
                Payment payment = paymentService.getPaymentInfo(impUid);
                paymentService.refund(impUid, payment);
                order.getUser().earnPoints(order.getUsedPoint());

                order.setStatus(OrderStatus.ORDER_CANCEL);

            } catch (IamportResponseException | IOException  e) {
                throw new InvalidCustomException(ORDER_CANCEL_FAIL);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackStock(Orders order) {
        List<OrderedProduct> orderedProducts = order.getOrderedProducts();

        orderedProducts.forEach((orderedProduct) -> {
            Product product = productRepository.findByIdWithLock(orderedProduct.getProductIdx())
                    .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_PRODUCT_NOT_FOUND));
            product.increaseStock(orderedProduct.getQuantity());

        });
    }

    public Page<CompanyOrderListResponse> companyOrderList(Company company, Integer page, String status, Integer month) {
        Pageable pageable = PageRequest.of(page - 1, COMPANY_PAGE_SIZE, Sort.Direction.DESC, "idx");

        Page<Orders> orders = ordersRepository.historyWithPaging(company, pageable, status, month);
        return orders.map(order -> {
            String title = productBoardRepository.findById(order.getBoardIdx())
                    .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_EVENT_NOT_FOUND)).getTitle();
            return order.toCompanyOrderListResponse(title);
        });
    }

    public CompanyOrderDetailResponse companyOrderDetail(Company company, Long orderIdx) {
        Orders order = ordersRepository.findById(orderIdx)
                .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_DETAIL));

        ProductBoard board = productBoardRepository.findById(order.getBoardIdx())
                .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_EVENT_NOT_FOUND));

        if (company.getIdx() != board.getCompany().getIdx()) {
            throw new InvalidCustomException(ORDER_FAIL_DETAIL);
        }

        List<OrderedProduct> orederdProducts = order.getOrderedProducts();
        List<OrderedProductResponse> products = orederdProducts.stream().map(orderdProduct ->{
            Product product = productRepository.findById(orderdProduct.getProductIdx()).orElseThrow();
            return orderdProduct.toOrderedProductResponse(product, board.getDiscountRate());
        }
        ).collect(Collectors.toList());

        return order.toCompanyOrderDetailResponse(products);
    }

    public Page<UserOrderListResponse> userOrderList(User user, Integer page, String status, Integer month) {
        Pageable pageable = PageRequest.of(page - 1, USER_PAGE_SIZE, Sort.Direction.DESC, "idx");

        Page<Orders> orders = ordersRepository.historyWithPaging(user, pageable, status, month);
        return orders.map(order -> {
            ProductBoard board = productBoardRepository.findById(order.getBoardIdx())
                    .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_EVENT_NOT_FOUND));
            return order.toUserOrderListResponse(board);
        });
    }

    public UserOrderDetailResponse userOrderDetail(User user, Long orderIdx) {
        Orders order = ordersRepository.findById(orderIdx)
                .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_DETAIL));

        if (user.getIdx() != order.getUser().getIdx()) {
            throw new InvalidCustomException(ORDER_FAIL_DETAIL);
        }

        ProductBoard board = productBoardRepository.findById(order.getBoardIdx())
                .orElseThrow(() -> new InvalidCustomException(ORDER_FAIL_EVENT_NOT_FOUND));

        return order.toUserOrderDetailResponse(board);
    }
}
