package org.example.backend.domain.user.service;

import jakarta.mail.internet.MimeMessage;
import org.example.backend.domain.user.model.dto.UserAuthTokenDto;
import org.example.backend.domain.user.model.entity.UserAuthToken;
import org.example.backend.domain.user.repository.UserAuthTokenRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAuthTokenServiceTest {

    @InjectMocks
    private UserAuthTokenService userAuthTokenService;

    @Mock
    private UserAuthTokenRepository userAuthTokenRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    @DisplayName("이메일 인증 요청 시 정상적으로 토큰 생성 및 이메일 발송이 이루어진다.")
    @Test
    void doAuth_Success() {
        // given
        UserAuthTokenDto.UserEmailAuthRequest request = UserAuthTokenDto.UserEmailAuthRequest.builder()
                .email("test@example.com")
                .build();

        // when
        doNothing().when(userAuthTokenRepository).deleteAllByEmail(request.getEmail());  // 이메일로 기존 토큰 삭제
        when(userAuthTokenRepository.save(any(UserAuthToken.class))).thenReturn(new UserAuthToken());  // 새로운 토큰 저장
        when(mailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));  // 메일 객체 생성

        // 정상적인 메일 발송
        doNothing().when(mailSender).send(any(MimeMessage.class));  // 메일 발송 처리 (void 메서드이므로 doNothing() 사용)

        // then
        Boolean result = userAuthTokenService.doAuth(request);

        // 결과 검증
        assertTrue(result);
        verify(userAuthTokenRepository, times(1)).deleteAllByEmail(request.getEmail());
        verify(userAuthTokenRepository, times(1)).save(any(UserAuthToken.class));  // save 메서드 호출 검증
        verify(mailSender, times(1)).send(any(MimeMessage.class));  // 메일 발송 검증
    }

    @DisplayName("이메일 인증 요청 시 이메일 발송 실패 예외 처리")
    @Test
    void doAuth_Fail_SendEmail() {
        // given
        UserAuthTokenDto.UserEmailAuthRequest request = UserAuthTokenDto.UserEmailAuthRequest.builder()
                .email("test@example.com")
                .build();

        // when
        doNothing().when(userAuthTokenRepository).deleteAllByEmail(request.getEmail());  // 이메일로 기존 토큰 삭제
        when(userAuthTokenRepository.save(any(UserAuthToken.class))).thenReturn(new UserAuthToken());  // 새로운 토큰 저장
        when(mailSender.createMimeMessage()).thenReturn(Mockito.mock(MimeMessage.class));  // 메일 객체 생성

        // 메일 발송 실패 시 예외 발생하도록 mock 설정
        doThrow(new InvalidCustomException(BaseResponseStatus.EMAIL_VERIFY_FAIL)).when(mailSender).send(any(MimeMessage.class));

        // then
        try {
            userAuthTokenService.doAuth(request);
        } catch (InvalidCustomException e) {
            assertTrue(e.getMessage().contains("인증메일 발송에 실패했습니다"));
        }

        verify(userAuthTokenRepository, times(1)).deleteAllByEmail(request.getEmail());
        verify(userAuthTokenRepository, times(1)).save(any(UserAuthToken.class));  // save 메서드 호출 검증
        verify(mailSender, times(1)).send(any(MimeMessage.class));  // 메일 발송 검증
    }
}