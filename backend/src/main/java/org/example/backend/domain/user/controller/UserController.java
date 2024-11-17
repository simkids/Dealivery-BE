package org.example.backend.domain.user.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.model.dto.UserAuthTokenDto;
import org.example.backend.domain.user.model.dto.UserDto;
import org.example.backend.domain.user.service.UserAuthTokenService;
import org.example.backend.domain.user.service.UserService;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.common.constants.SwaggerDescription;
import org.example.backend.global.common.constants.SwaggerExamples;
import org.example.backend.global.security.custom.model.dto.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthTokenService userAuthTokenService;

    @Operation(summary = "일반회원가입 API", description = SwaggerDescription.USER_SIGNUP_REQUEST,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = SwaggerExamples.USER_SIGNUP_REQUEST)}
                    )
            ))
    @PostMapping("/signup")
    public BaseResponse signup(
            @Valid @RequestBody UserDto.UserSignupRequest request
            ){
        //이미 가입됐는지 체크
        userService.isExist(request.getEmail());
        //이메일 인증 코드 검증
        if (!userAuthTokenService.isTokenValid(request.getEmailCode(), request.getEmail())){
            return new BaseResponse(BaseResponseStatus.USER_SIGNUP_FAIL_INVALID_EMAIL_CODE);
        }
        if (!userService.signup(request)){
            return new BaseResponse(BaseResponseStatus.USER_SIGNUP_FAIL);
        }

        return new BaseResponse();
    }

    @Operation(summary = "일반 이메일 인증요청 API", description = SwaggerDescription.EMAIL_AUTH_REQUEST,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = SwaggerExamples.EMAIL_AUTH_REQUEST)}
                    )
            ))
    @PostMapping("/email/verify")
    public BaseResponse emailVerify(
            @RequestBody UserAuthTokenDto.UserEmailAuthRequest userEmailAuthRequest
            ){
        if (userAuthTokenService.doAuth(userEmailAuthRequest)){
            return new BaseResponse();
        }
        return new BaseResponse(BaseResponseStatus.FAIL);
    }

    @PostMapping("/social/signup")
    public BaseResponse socialSignup(
            @Valid @RequestBody UserDto.SocialSignupRequest socialSignupRequest
    ){
        //이미 가입됐는지 체크
        userService.isExist(socialSignupRequest.getEmail());
        if (!userService.socialSignup(socialSignupRequest)){
            return new BaseResponse(BaseResponseStatus.USER_SIGNUP_FAIL);
        }

        return new BaseResponse();
    }

    @GetMapping("/detail")
    public BaseResponse<UserDto.UserDetailResponse> getDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        UserDto.UserDetailResponse userDetailResponse =
                userService.getDetail(userDetails.getUsername(),userDetails.getIdx());
        return new BaseResponse<>(userDetailResponse);
    }

    @PutMapping("/edit")
    public BaseResponse editDetail(
            @Valid @RequestBody UserDto.UserDetailEditRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        userService.editDetail(userDetails.getIdx(),request);
        return new BaseResponse();
    }
}
