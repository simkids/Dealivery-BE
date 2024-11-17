package org.example.backend.global.security.custom.auth;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.backend.global.security.custom.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailService customUserDetailService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 커스텀 토큰이 전달되었는지 확인
        if (authentication instanceof CustomAuthenticationToken) {
            CustomAuthenticationToken customToken = (CustomAuthenticationToken) authentication;
            String email = (String) customToken.getPrincipal();
            String password = (String) customToken.getCredentials();
            String type = customToken.getType();  // 타입 정보

            // 이메일과 타입에 따라 사용자 로드
            UserDetails userDetails = (UserDetails) customUserDetailService.loadUserByUsername(email, type);

            // 패스워드 검증
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }

            // 인증 성공 시, 인증된 토큰 반환
            return new CustomAuthenticationToken(userDetails, password, userDetails.getAuthorities(), type);
        }

        // 기본적인 경우 처리
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // CustomTypeAuthenticationToken만 처리
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
