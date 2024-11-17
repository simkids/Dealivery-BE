package org.example.backend.domain.qna.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.qna.model.dto.AnswerDto;
import org.example.backend.domain.qna.service.AnswerService;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qna/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "답변 등록 API", description = "기업회원이 문의에 대한 답변을 등록합니다.")
    @PostMapping("/create")
    public BaseResponse createAnswer(@RequestBody AnswerDto.AnswerCreateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername(); // 인증된 기업회원 이메일

            // 답변 등록 서비스 호출
            answerService.createAnswer(request, email);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (InvalidCustomException e) {
            return new BaseResponse<>(e.getStatus());
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.FAIL);
        }
    }

    @Operation(summary = "답변 삭제 API", description = "답변을 삭제합니다.")
    @DeleteMapping("/delete/{id}")
    public BaseResponse deleteAnswer(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername(); // 인증된 기업회원 이메일
            answerService.deleteAnswer(id, email);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (InvalidCustomException e) {
            return new BaseResponse<>(e.getStatus());
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.FAIL);
        }
    }
}
