package org.example.backend.domain.user.service;

import org.example.backend.domain.delivery.repository.DeliveryRepository;
import org.example.backend.domain.user.model.dto.UserDto;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.common.constants.BaseResponseStatus;
import org.example.backend.global.exception.InvalidCustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @DisplayName("[해피 케이스] 이메일을 넘겨받아 이미 존재하는 회원인지 검증한다.")
    @Test
    void isExist_Success() {
        // given
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertDoesNotThrow(() -> userService.isExist(email));

        // 검증
        verify(userRepository, times(1)).findByEmail(email);
    }

    @DisplayName("[해피 케이스] 이메일과 idx로 유저 상세 정보를 조회한다.")
    @Test
    void getDetail_Success() {
        // given
        String email = "test@example.com";
        Long idx = 1L;

        User user = User.builder()
                .idx(idx)
                .email(email)
                .name("홍길동")
                .address("서울시 강남구")
                .addressDetail("101호")
                .phoneNumber("010-1234-5678")
                .postNumber("12345")
                .point(1000L)
                .deliveries(new ArrayList<>())
                .build();

        when(userRepository.findByEmailAndIdx(email, idx)).thenReturn(Optional.of(user));

        // when
        UserDto.UserDetailResponse response = userService.getDetail(email, idx);

        // then
        assertEquals(email, response.getEmail());
        assertEquals("홍길동", response.getName());
        assertEquals("서울시 강남구", response.getAddress());
        assertEquals("101호", response.getAddressDetail());
        assertEquals("010-1234-5678", response.getPhoneNumber());
        assertEquals(1000L, response.getPoint());

        verify(userRepository, times(1)).findByEmailAndIdx(email, idx);
    }

    @DisplayName("[해피 케이스] 회원 가입 필수항목 값들을 넘겨받아 회원을 생성한다.")
    @Test
    void signup_Success() {
        // given
        UserDto.UserSignupRequest request = UserDto.UserSignupRequest.builder()
                .email("test@example.com")
                .name("홍길동")
                .emailCode("123456")
                .password("Qwer1234!")
                .phoneNumber("010-1234-5678")
                .postNumber("12345")
                .address("서울시 강남구")
                .addressDetail("101호")
                .type("NORMAL")
                .build();

        User user = request.toEntity("encodedPassword");
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(deliveryRepository.save(any())).thenReturn(null);

        // when
        boolean result = userService.signup(request);

        // then
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(deliveryRepository, times(1)).save(any());
    }

    @DisplayName("[해피 케이스]소셜 회원 가입에 필요한 추가정보를 넘겨받아 회원을 생성한다.")
    @Test
    void socialSignup_Success() {
        // given
        UserDto.SocialSignupRequest request = UserDto.SocialSignupRequest.builder()
                .email("social@example.com")
                .name("소셜유저")
                .phoneNumber("010-5678-1234")
                .postNumber("54321")
                .address("서울시 서초구")
                .addressDetail("202호")
                .type("SOCIAL")
                .build();

        User user = request.toEntity();
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(deliveryRepository.save(any())).thenReturn(null);

        // when
        boolean result = userService.socialSignup(request);

        // then
        assertTrue(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(deliveryRepository, times(1)).save(any());
    }

    @DisplayName("[해피 케이스] 수정된 정보를 넘겨받아 회원 정보를 수정한다.")
    @Test
    void editDetail_Success() {
        // given
        Long idx = 1L;
        UserDto.UserDetailEditRequest request = UserDto.UserDetailEditRequest.builder()
                .phoneNumber("010-9876-5432")
                .address("부산시 해운대구")
                .addressDetail("303호")
                .postNumber("67890")
                .build();

        User user = User.builder()
                .idx(idx)
                .phoneNumber("010-1234-5678")
                .address("서울시 강남구")
                .addressDetail("101호")
                .postNumber("12345")
                .build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.of(user));

        // when
        userService.editDetail(idx, request);

        // then
        assertEquals("010-9876-5432", user.getPhoneNumber());
        assertEquals("부산시 해운대구", user.getAddress());
        assertEquals("303호", user.getAddressDetail());
        assertEquals("67890", user.getPostNumber());

        verify(userRepository, times(1)).save(user);
    }

    @DisplayName("[예외 케이스]이메일이 중복되어 이미 이메일이 존재한다는 예외를 발생시킨다.")
    @Test
    void isExist_Failure() {
        // given
        String email = "duplicate@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        // when & then
        InvalidCustomException exception = assertThrows(InvalidCustomException.class, () -> {
            userService.isExist(email);
        });

        assertEquals(BaseResponseStatus.USER_SIGNUP_FAIL_ALREADY_EXIST.getMessage(), exception.getMessage());
    }

    @DisplayName("[예외 케이스] 존재하지 않는 이메일과 idx로 유저 상세 정보를 조회하면 예외가 발생한다.")
    @Test
    void getDetail_Failure() {
        // given
        String email = "notfound@example.com";
        Long idx = 999L;

        when(userRepository.findByEmailAndIdx(email, idx)).thenReturn(Optional.empty());

        // when & then
        InvalidCustomException exception = assertThrows(InvalidCustomException.class, () -> {
            userService.getDetail(email, idx);
        });

        assertEquals(BaseResponseStatus.USER_DETAIL_FAIL_USER_NOT_FOUND.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).findByEmailAndIdx(email, idx);
    }


    @DisplayName("[예외 케이스] 존재하지 않는 idx로 회원 정보를 수정하려 하면 예외가 발생한다.")
    @Test
    void editDetail_Failure() {
        // given
        Long idx = 999L;
        UserDto.UserDetailEditRequest request = UserDto.UserDetailEditRequest.builder()
                .phoneNumber("010-9876-5432")
                .address("부산시 해운대구")
                .addressDetail("303호")
                .postNumber("67890")
                .build();

        when(userRepository.findByIdx(idx)).thenReturn(Optional.empty());

        // when & then
        InvalidCustomException exception = assertThrows(InvalidCustomException.class, () -> {
            userService.editDetail(idx, request);
        });

        assertEquals(BaseResponseStatus.USER_DETAIL_EDIT_FAIL_USER_NOT_FOUND.getMessage(), exception.getMessage());
        verify(userRepository, times(1)).findByIdx(idx);
        verify(userRepository, never()).save(any(User.class)); // 저장 메서드는 호출되지 않아야 함
    }
}