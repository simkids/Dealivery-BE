package org.example.backend.domain.qna.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.qna.model.entity.Answer;
import org.example.backend.domain.qna.model.entity.Question;

import java.time.LocalDateTime;

public class AnswerDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerCreateRequest {
        private Long questionIdx;
        private String content;

        public Answer toEntity(Company company, Question question) {
            return Answer.builder()
                    .content(this.content)
                    .company(company)
                    .question(question)
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnswerResponse {
        private Long idx;
        private String content;
        private String companyName;
        private LocalDateTime createdAt;
    }
}
