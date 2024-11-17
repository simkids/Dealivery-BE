package org.example.backend.domain.qna.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.qna.model.entity.Question;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.AnswerStatus;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionCreateRequest{
        @NotBlank(message = "제목은 공백일 수 없습니다.")
        private String title;
        @NotBlank(message = "내용은 공백일 수 없습니다.")
        private String content;
        @NotNull(message = "게시글 ID는 필수입니다.")
        private Long productBoardIdx;

        public Question toEntity(User user, ProductBoard productBoard) {
            return Question.builder()
                    .title(this.title)
                    .content(this.content)
                    .user(user)
                    .productBoard(productBoard)
                    .createdAt(LocalDateTime.now())
                    .answerStatus(AnswerStatus.ANSWER_WAITING.getStatus())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionCreateResponse{
        private Long idx;
        private String title;
        private String content;
        private String userName;
        private String answerStatus;
        private LocalDateTime createdAt;

        private String answerContent;
        private LocalDateTime answerCreatedAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionListResponse {
        private Long idx;
        private String title;
        private String content;
        private String userName;
        private String answerStatus;
        private LocalDateTime createdAt;
        private String email;
        private Long productBoardIdx;
        private String productTitle;

        private List<AnswerDto.AnswerResponse> answers;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionUpdateRequest {
        private String title;
        private String content;
    }
}
