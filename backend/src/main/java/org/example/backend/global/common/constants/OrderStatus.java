package org.example.backend.global.common.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum OrderStatus {

    PAYMENT_WAIT("결제 대기"),
    ORDER_COMPLETE("주문 완료"),
    ORDER_FAIL("주문 실패"),
    ORDER_CANCEL("주문 취소");
    private final String status;
    OrderStatus(final String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static OrderStatus of(final String parameter) {
        return Arrays.stream(values())
                .filter(payment -> payment.status.equals(parameter))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("주문 상태가 잘못되었습니다."));
    }
}
