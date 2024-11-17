package org.example.backend.domain.company.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.company.model.dto.CompanyAuthTokenDto;
import org.example.backend.domain.company.model.dto.CompanyDto;
import org.example.backend.domain.company.service.CompanyAuthTokenService;
import org.example.backend.domain.company.service.CompanyRegisterVerifyService;
import org.example.backend.domain.company.service.CompanyService;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.common.constants.SwaggerDescription;
import org.example.backend.global.common.constants.SwaggerExamples;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyAuthTokenService companyAuthTokenService;
    private final CompanyRegisterVerifyService companyRegisterVerifyService;
    private final CompanyService companyService;

    @Operation(summary = "업체회원가입 API", description = SwaggerDescription.COMPANY_SIGNUP_REQUEST,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(value = SwaggerExamples.COMPANY_SIGNUP_REQUEST)}
            )
        ))
    @PostMapping("/signup")
    public BaseResponse signup(
        @Valid @RequestBody CompanyDto.CompanySignupRequest request
    ){
        //이미 가입됐는지 체크
        if (!companyService.isExist(request.getEmail())){
            throw new InvalidCustomException(BaseResponseStatus.USER_SIGNUP_FAIL_ALREADY_EXIST);
        }
        //이메일 인증 코드 검증
        if (!companyAuthTokenService.isTokenValid(request.getEmailCode(), request.getEmail())){
            throw new InvalidCustomException(BaseResponseStatus.USER_SIGNUP_FAIL_INVALID_EMAIL_CODE);
        }
        companyRegisterVerifyService.verifyRegNumber(request);
        companyService.signup(request);

        return new BaseResponse();
    }

    @Operation(summary = "업체 이메일 인증 API", description = SwaggerDescription.EMAIL_AUTH_REQUEST,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(value = SwaggerExamples.EMAIL_AUTH_REQUEST)}
            )
        ))
    @PostMapping("/email/verify")
    public BaseResponse emailVerify(
            @Valid @RequestBody CompanyAuthTokenDto.CompanyEmailAuthRequest companyEmailAuthRequest
            ){
        if (!companyAuthTokenService.doAuth(companyEmailAuthRequest)){
            return new BaseResponse(BaseResponseStatus.FAIL);
        }
        return new BaseResponse();
    }
}