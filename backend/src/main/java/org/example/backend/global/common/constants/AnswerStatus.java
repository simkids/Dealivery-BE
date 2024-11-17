package org.example.backend.global.common.constants;

import lombok.Getter;

@Getter
public enum AnswerStatus {
    ANSWER_WAITING("답변대기"),
    ANSWER_COMPLETED("답변완료");

    private final String status;

    AnswerStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.status;
    }
}
