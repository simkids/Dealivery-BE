package org.example.backend.domain.qna.repository;

import org.example.backend.domain.qna.model.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // 특정 문의에 달린 모든 답변을 리스트로 반환
    List<Answer> findAllByQuestionIdx(Long questionIdx);
}
