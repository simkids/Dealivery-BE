package org.example.backend.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.backend.domain.company.model.entity.Company;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.global.security.jwt.JwtUtil;
import org.example.backend.global.security.custom.model.dto.CustomCompanyDetails;
import org.example.backend.global.security.custom.model.dto.CustomUserDetails;
import org.example.backend.global.security.jwt.model.entity.CompanyRefreshToken;
import org.example.backend.global.security.jwt.model.entity.UserRefreshToken;
import org.example.backend.global.security.jwt.repository.CompanyRefreshTokenRepository;
import org.example.backend.global.security.jwt.repository.UserRefreshTokenRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CompanyRefreshTokenRepository companyRefreshTokenRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie accessTokenCookie = null;
        Cookie refreshTokenCookie = null;
        Cookie typeCookie = null;
        Cookie[] cookieArray = request.getCookies();
        //쿠키가 없을 시 필터 통과
        if (cookieArray == null) {
            filterChain.doFilter(request, response);
            return;
        }
        //쿠키 매핑
        for (Cookie cookie : cookieArray) {
            if (cookie.getName().equals("AToken")) {
                accessTokenCookie =  cookie;
            }
            if (cookie.getName().equals("RToken")){
                refreshTokenCookie = cookie;
            }
            if (cookie.getName().equals("type")) {
                typeCookie = cookie;
            }
        }
        //AccessToken이 없을 경우 필터 통과
        if (accessTokenCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }
        //AccessToken이 만료됐을 경우 refresh token으로 재발급 진행
        String accessToken = accessTokenCookie.getValue();
        if (jwtUtil.isExpired(accessToken)) {
            log.info("AccessToken 만료");

            // 만료되었는데 refresh token도 안가지고 있는 경우
            if (refreshTokenCookie == null) {
                filterChain.doFilter(request, response);
                log.info("RefreshToken 없음");
                return;
            }
            // refresh token을 가지고 있는 경우 (타입에 맞게 만료 확인 후 재발급 진행)
            if (!isExpiredRefreshToken(refreshTokenCookie)){
                return;
            }
            String reissuedAccessToken = reissueAccessToken(refreshTokenCookie.getValue(), typeCookie.getValue());
            //재발급중 문제가 발생한경우 로그 남기고 통과
            if (reissuedAccessToken == null) {
                filterChain.doFilter(request, response);
                log.info("RefreshToken 문제발생");
                return;
            }
            //문제 없을 경우 엑세스 토큰 쿠키 갱신
            accessToken = reissuedAccessToken;
            Cookie aToken = new Cookie("AToken", accessToken);
            aToken.setPath("/");
            aToken.setHttpOnly(true);
            aToken.setSecure(true);
            response.addCookie(aToken);
        }
        authorize(accessToken, typeCookie.getValue());
        filterChain.doFilter(request, response);
    }

    //정상적인 accessToken으로 인증 인가 처리후 다음필터 실행
    private void authorize(String accessToken, String type) {
        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);
        Long idx = jwtUtil.getIdx(accessToken);

        if (type.equals("user")){
            User user = User.builder()
                    .idx(idx)
                    .email(email)
                    .role(role)
                    .emailStatus(true)
                    .build();
            CustomUserDetails customUserDetails = new CustomUserDetails(user);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        if (type.equals("company")){
            Company company = Company.builder()
                    .idx(idx)
                    .email(email)
                    .role(role)
                    .emailStatus(true)
                    .regStatus(true)
                    .build();
            CustomCompanyDetails customCompanyDetails = new CustomCompanyDetails(company);
            Authentication authToken = new UsernamePasswordAuthenticationToken(customCompanyDetails, null, customCompanyDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    //회원 타입에 맞게 refresh token을 확인해서 access token을 재발급하는 함수
    private String reissueAccessToken(String refreshToken, String type) {
        String email = "";
        String existRefreshToken = "";

        if (type.equals("user")){
            email = jwtUtil.getEmail(refreshToken);
            UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByEmail(email).orElseThrow();
            existRefreshToken = userRefreshToken.getRefreshToken();

        }
        if (type.equals("company")){
            email = jwtUtil.getEmail(refreshToken);
            CompanyRefreshToken companyRefreshToken = companyRefreshTokenRepository.findByEmail(email).orElseThrow();
            existRefreshToken = companyRefreshToken.getRefreshToken();
        }

        if (!existRefreshToken.equals("") && jwtUtil.isValid(existRefreshToken) && existRefreshToken.equals(refreshToken)){
            return jwtUtil.createAccessToken(jwtUtil.getIdx(existRefreshToken),
                    jwtUtil.getEmail(existRefreshToken), jwtUtil.getRole(existRefreshToken));
        }
        return null;
    }

    //refresh token 만료여부 확인
    private Boolean isExpiredRefreshToken(Cookie refreshTokenCookie){
        String refreshToken = refreshTokenCookie.getValue();
        if (jwtUtil.isExpired(refreshToken)) {
            log.info("RefreshToken 만료");
            return false;
        }
        return true;
    }
}
