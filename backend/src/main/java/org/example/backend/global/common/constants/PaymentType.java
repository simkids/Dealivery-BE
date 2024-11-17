package org.example.backend.global.common.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum PaymentType {
    KAKAO_PAY("kakaopay"),
    TOSS_PAY("tosspay");
    private final String type;
    PaymentType(final String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }

    @JsonCreator
    public static PaymentType of(final String parameter) {
        return Arrays.stream(values())
                .filter(payment -> payment.type.equals(parameter))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("결제 유형이 잘못되었습니다."));
    }
}
