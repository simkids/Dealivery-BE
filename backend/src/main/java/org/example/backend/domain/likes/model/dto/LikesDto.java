package org.example.backend.domain.likes.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.board.model.entity.ProductBoard;
import org.example.backend.domain.likes.model.entity.Likes;
import org.example.backend.domain.user.model.entity.User;

public class LikesDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeRequest{
        @NotNull
        private Long productBoardIdx;

        public Likes toEntity(ProductBoard productBoard, User user){
            return Likes.builder()
                    .productBoard(productBoard)
                    .user(user)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeResponse {
        private Long idx;
        private String productThumbnailUrl;
        private String title;
        private LocalDateTime startedAt;
        private LocalDateTime endedAt;
        private String companyName;
        private String category;
        private Integer price;
        private Integer discountRate;
    }
}
