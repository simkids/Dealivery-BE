package org.example.backend.domain.company.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.company.model.entity.CompanyAuthToken;
import org.example.backend.domain.user.model.entity.UserAuthToken;

import java.time.LocalDateTime;

public class CompanyAuthTokenDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyEmailAuthRequest{
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        private String email;
        public CompanyAuthToken toEntity(String token, LocalDateTime expiredTime){
            return CompanyAuthToken.builder()
                    .email(this.email)
                    .token(token)
                    .expiredAt(expiredTime)
                    .build();
        }
    }
}
