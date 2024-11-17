package org.example.backend.domain.delivery.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.delivery.model.entity.Delivery;
import org.example.backend.domain.user.model.entity.User;

public class DeliveryDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeliveryResponse{
        private Long idx;

        private String name;

        private Boolean isDefault;

        private String address;

        private String addressDetail;

        private String postNumber;

    }
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateDeliveryRequest{
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String name;
        @NotNull
        private Boolean isDefault;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String address;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String addressDetail;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String postNumber;

        public Delivery toEntity(User user){
            return Delivery.builder()
                    .address(this.address)
                    .name(this.name)
                    .addressDetail(this.addressDetail)
                    .isDefault(this.isDefault)
                    .postNumber(this.postNumber)
                    .user(user)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SetDefaultRequest{
        @NotNull
        private Long idx;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EditDeliveryRequest{
        @NotNull
        private Long idx;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String name;
        @NotNull
        private Boolean isDefault;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String address;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String addressDetail;
        @NotBlank
        @Pattern(regexp = "^(?!.*[!@#$%^&*()_+={}:;\"'<>,.?/~`|\\\\-])(?=.*[^\\n])[^\\nㄱ-ㅎ]*$")
        private String postNumber;

    }


}
