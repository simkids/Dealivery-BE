package org.example.backend.domain.company.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.company.model.entity.CompanyRegisterVerify;
import org.example.backend.global.common.constants.Role;

import java.time.LocalDateTime;

public class CompanyDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CompanySignupRequest{
        @NotBlank
        @Size(max = 10)
        private String name;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        @Size(max = 40)
        private String email;

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
        @Size(max = 30)
        private String companyName;

        @NotBlank
        @Pattern(regexp = "^\\d{10}$")
        private String regNumber;

        @NotBlank
        @Pattern(regexp = "^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$")
        private String openedAt;

        @NotBlank
        @Pattern(regexp = "^\\d{4}-[가-힣]{4}-\\d{4}$")
        private String mosNumber;

        public Company toEntity(String encodedPassword){
            return Company.builder()
                    .name(this.name)
                    .email(this.email)
                    .password(encodedPassword)
                    .address(this.address)
                    .addressDetail(this.addressDetail)
                    .emailStatus(true)
                    .status(true)
                    .regStatus(true)
                    .role(Role.ROLE_COMPANY.getRole())
                    .phoneNumber(this.phoneNumber)
                    .postNumber(this.postNumber)
                    .companyName(this.companyName)
                    .regNumber(this.regNumber)
                    .mosNumber(this.mosNumber)
                    .openedAt(this.openedAt)
                    .registeredAt(LocalDateTime.now())
                    .build();
        }

        public CompanyRegisterVerify toEntity(){
            return CompanyRegisterVerify.builder()
                    .regNumber(this.regNumber)
                    .status(true)
                    .expiredAt(LocalDateTime.now().plusMinutes(10))
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegNumberVerifyRequest{
        private String b_no;
        private String p_nm;
        private String start_dt;
    }
}
