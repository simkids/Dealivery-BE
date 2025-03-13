package org.example.backend.domain.orders.service;

import static org.example.backend.global.common.constants.BaseResponseStatus.ORDER_CREATE_FAIL_LACK_STOCK;
import static org.example.backend.global.common.constants.BaseResponseStatus.ORDER_FAIL_PRODUCT_NOT_FOUND;
import static org.example.backend.global.common.constants.BaseResponseStatus.ORDER_PAYMENT_CANCEL_FAILED;
import static org.example.backend.global.common.constants.BaseResponseStatus.ORDER_VALIDATION_FAIL_PRICE_MISMATCH;

import com.google.gson.Gson;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.board.product.model.entity.Product;
import org.example.backend.domain.board.product.repository.ProductRepository;
import org.example.backend.domain.orders.model.entity.OrderedProduct;
import org.example.backend.domain.orders.model.entity.Orders;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final IamportClient iamportClient;
    private final ProductRepository productRepository;

    public Payment getPaymentInfo(String impUid) throws IamportResponseException, IOException {
        IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(impUid);
        return iamportResponse.getResponse();
    }


    @Transactional
    public void validatePayment(Payment payment, Orders order) {

        String customData = payment.getCustomData();
        Gson gson = new Gson();
        Map<String, Object> data = gson.fromJson(customData, Map.class);

        Integer usedPoint = (Double.valueOf(data.get("usedPoint").toString())).intValue();
        Integer discountRate = (Double.valueOf(data.get("discountRate").toString())).intValue();
        Integer amount = payment.getAmount().intValue();

        AtomicLong totalPrice = validateAndDecreaseStock(payment, usedPoint, order, discountRate);

        if (usedPoint > order.getUser().getPoint()) {
            refund(payment.getImpUid(), payment);
            throw new InvalidCustomException(ORDER_VALIDATION_FAIL_PRICE_MISMATCH);
        }

       // if (amount != totalPrice.intValue()) {
       //     refund(payment.getImpUid(), payment);
       //     throw new InvalidCustomException(ORDER_VALIDATION_FAIL_PRICE_MISMATCH);
       // }

    }

    public AtomicLong validateAndDecreaseStock(Payment payment, Integer usedPoint, Orders order, Integer discountRate) {

        AtomicLong totalPrice = new AtomicLong(-usedPoint.longValue()); // 결제해야할 총 금액

        List<OrderedProduct> orderedProducts = order.getOrderedProducts();
        orderedProducts.forEach((orderdProduct) -> {
            Product product = productRepository.findByIdWithLock(orderdProduct.getProductIdx())
                        .orElseThrow(
                                () -> new InvalidCustomException(ORDER_FAIL_PRODUCT_NOT_FOUND)); // 해당하는 상품을 찾을 수가 없을 때

            if (orderdProduct.getQuantity() > product.getStock()) {
                refund(payment.getImpUid(), payment);
                throw new InvalidCustomException(ORDER_CREATE_FAIL_LACK_STOCK); // 재고 수량 없을 때
            }

            product.decreaseStock(orderdProduct.getQuantity()); // 재고 수량 변경
            productRepository.save(product);


            long originalPrice = product.getPrice();
            int quantity = orderdProduct.getQuantity();
            totalPrice.updateAndGet(v -> (long) (v + (originalPrice * quantity * (1 - discountRate / 100.0))));

        });

        return totalPrice;
    }

    public IamportResponse refund(String impUid, Payment info)  {
        CancelData cancelData = new CancelData(impUid, true, info.getAmount());
        try {
            return iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException e) {
            throw new InvalidCustomException(ORDER_PAYMENT_CANCEL_FAILED);
        }
    }
}
