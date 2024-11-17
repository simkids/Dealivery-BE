package org.example.backend.domain.user.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.delivery.model.dto.DeliveryDto;
import org.example.backend.domain.delivery.model.entity.Delivery;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.common.constants.Role;

import java.time.LocalDateTime;
import java.util.List;

public class UserDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserSignupRequest{

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        @Size(max = 40)
        private String email;

        @NotBlank
        @Size(max = 30)
        private String name;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]{6}$")
        private String emailCode;

        @NotBlank
        @Size(max = 20)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#])[A-Za-z\\d~!@#]{8,}$")
        private String password;

        @NotBlank
        @Pattern(regexp = "^(010)-\\d{4}-\\d{4}$")
        private String phoneNumber;

        @NotBlank
        @Size(max = 6)
        @Pattern(regexp = "^\\d{5,}$")
        private String postNumber;

        @NotBlank
        @Size(max = 30)
        private String address;

        @NotBlank
        @Size(max = 30)
        private String addressDetail;

        @NotBlank
        private String type;

        public User toEntity(String encodedPassword){
            return User.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(encodedPassword)
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .emailStatus(true)
                    .status(true)
                    .role(Role.ROLE_USER.getRole())
                    .phoneNumber(this.phoneNumber)
                    .postNumber(this.postNumber)
                    .type(this.type)
                    .registeredAt(LocalDateTime.now())
                    .point(0L)
                    .build();
        }

        public Delivery toDeliveryEntity(User user){
            return Delivery.builder()
                    .name("집")
                    .user(user)
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .postNumber(this.postNumber)
                    .isDefault(true)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserLoginRequest{
        private String email;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SocialSignupRequest{
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        @Size(max = 40)
        private String email;

        @NotBlank
        @Size(max = 30)
        private String name;

        @NotBlank
        @Pattern(regexp = "^(010)-\\d{4}-\\d{4}$")
        private String phoneNumber;

        @NotBlank
        @Size(max = 6)
        @Pattern(regexp = "^\\d{5,}$")
        private String postNumber;

        @NotBlank
        @Size(max = 30)
        private String address;

        @NotBlank
        @Size(max = 30)
        private String addressDetail;

        @NotBlank
        private String type;

        public User toEntity(){
            return User.builder()
                    .name(this.name)
                    .email(this.email)
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .emailStatus(true)
                    .status(true)
                    .role(Role.ROLE_USER.getRole())
                    .phoneNumber(this.phoneNumber)
                    .postNumber(this.postNumber)
                    .type(this.type)
                    .registeredAt(LocalDateTime.now())
                    .point(0L)
                    .build();
        }

        public Delivery toDeliveryEntity(User user){
            return Delivery.builder()
                    .name("집")
                    .user(user)
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .postNumber(this.postNumber)
                    .isDefault(true)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDetailResponse{
        private Long userIdx;
        private String name;
        private String email;
        private String address;
        private String addressDetail;
        private String postNumber;
        private String phoneNumber;
        private List<DeliveryDto.DeliveryResponse> deliveries;
        private Long point;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDetailEditRequest{
        @NotBlank
        @Pattern(regexp = "^(010)-\\d{4}-\\d{4}$")
        private String phoneNumber;

        @NotBlank
        @Size(max = 30)
        private String address;

        @NotBlank
        @Size(max = 30)
        private String addressDetail;

        @NotBlank
        @Size(max = 6)
        @Pattern(regexp = "^\\d{5,}$")
        private String postNumber;
    }

}
