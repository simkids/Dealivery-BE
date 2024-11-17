package org.example.backend.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.user.model.entity.UserAuthToken;
import java.time.LocalDateTime;
public class UserAuthTokenDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserEmailAuthRequest{
        private String email;
        public UserAuthToken toEntity(String token, LocalDateTime expiredTime){
            return UserAuthToken.builder()
                    .email(this.email)
                    .token(token)
                    .expiredAt(expiredTime)
                    .build();
        }
    }
}
