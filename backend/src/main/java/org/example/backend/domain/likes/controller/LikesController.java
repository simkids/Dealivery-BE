package org.example.backend.domain.likes.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.likes.model.dto.LikesDto;
import org.example.backend.domain.likes.service.LikesService;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.security.custom.model.dto.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikesService likesService;

    @PostMapping("")
    public BaseResponse like(
            @RequestBody LikesDto.LikeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ){
        likesService.like(request, userDetails.getIdx());
        return new BaseResponse();
    }

    @GetMapping("/list")
    public BaseResponse list(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam(value = "page", defaultValue = "1")Integer page) {
        Pageable pageable = PageRequest.of(page - 1, 4);
        if (userDetails == null) {
            return new BaseResponse(BaseResponseStatus.FAIL);
        }
        Page<LikesDto.LikeResponse> response = likesService.getLikes(userDetails.getIdx(), pageable);
        return new BaseResponse(response);
    }
}
