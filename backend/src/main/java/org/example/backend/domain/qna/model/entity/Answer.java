package org.example.backend.domain.qna.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.qna.model.dto.AnswerDto;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_idx")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_idx")
    private Question question;

    private LocalDateTime createdAt;

    // DTO 변환 메서드: 답변 조회 응답
    public AnswerDto.AnswerResponse toResponse() {
        return AnswerDto.AnswerResponse.builder()
                .idx(this.idx)
                .content(this.content)
                .companyName(this.company.getName())
                .createdAt(this.createdAt)
                .build();
    }
}
