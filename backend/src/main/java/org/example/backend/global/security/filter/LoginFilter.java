package org.example.backend.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.example.backend.domain.user.model.dto.UserDto;
import org.example.backend.global.common.constants.BaseResponse;
import org.example.backend.global.security.custom.auth.CustomAuthenticationToken;
import org.example.backend.global.security.jwt.JwtUtil;
import org.example.backend.global.security.custom.model.dto.CustomCompanyDetails;
import org.example.backend.global.security.custom.model.dto.CustomUserDetails;
import org.example.backend.global.security.jwt.model.entity.CompanyRefreshToken;
import org.example.backend.global.security.jwt.model.entity.UserRefreshToken;
import org.example.backend.global.security.jwt.repository.CompanyRefreshTokenRepository;
import org.example.backend.global.security.jwt.repository.UserRefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final Integer COOKIE_MAX_AGE = 60 * 60 * 24 * 30;

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CompanyRefreshTokenRepository companyRefreshTokenRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;

//    /login uri접속시 실행
//    ;를 구분자로 email,type 분리
//    type을 포함한 커스텀 authToken을 기반으로 인증절차 수행
//    커스텀 authToken을 커스텀 Provider가 받아서 인증 인가 처리
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserDto.UserLoginRequest userLoginRequest;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            userLoginRequest = objectMapper.readValue(messageBody, UserDto.UserLoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String email = userLoginRequest.getEmail().split(";")[0];
        String type = userLoginRequest.getEmail().split(";")[1];
        String password = userLoginRequest.getPassword();

        CustomAuthenticationToken authToken = new CustomAuthenticationToken(email, password, type);
        return authenticationManager.authenticate(authToken);
    }

    //인증 성공 처리(회원 유형에 맞는 토큰 발급)
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        //절차 시작
        processAuthentication(authResult, response);
    }

    //Provider를 통해 인증인가 후 받아온 객체 타입으로 user인지 company인지 구분하여 처리
    private void processAuthentication(Authentication authResult, HttpServletResponse response) throws IOException {
        // 로그인한 유저의 타입을 확인
        if (authResult.getPrincipal() instanceof CustomUserDetails) {
            handleUserDetails((CustomUserDetails) authResult.getPrincipal(), response);
        } else if (authResult.getPrincipal() instanceof CustomCompanyDetails) {
            handleCompanyDetails((CustomCompanyDetails) authResult.getPrincipal(), response);
        }
    }

    //일반회원 성공처리
    private void handleUserDetails(CustomUserDetails userDetails, HttpServletResponse response) throws IOException {
        String email = userDetails.getUsername();
        Long idx = userDetails.getIdx();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(idx, email, role);
        String refreshToken = jwtUtil.createRefreshToken(idx, email, role);

        // RefreshToken 엔티티 저장
        UserRefreshToken userRefreshToken = createUserRefreshTokenEntity(email, refreshToken);
        userRefreshTokenRepository.save(userRefreshToken);

        // 쿠키 설정
        createTokenCookies(response, accessToken, refreshToken, "user");

        // 응답 작성
        writeResponse(response, userDetails.getUser().getRole());
    }

    //업체회원 성공 처리
    private void handleCompanyDetails(CustomCompanyDetails companyDetails, HttpServletResponse response) throws IOException {
        String email = companyDetails.getUsername();
        Long idx = companyDetails.getIdx();
        String role = companyDetails.getAuthorities().iterator().next().getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(idx, email, role);
        String refreshToken = jwtUtil.createRefreshToken(idx, email, role);

        // RefreshToken 엔티티 저장
        CompanyRefreshToken companyRefreshToken = createCompanyRefreshTokenEntity(email, refreshToken);
        companyRefreshTokenRepository.save(companyRefreshToken);

        // 쿠키 설정
        createTokenCookies(response, accessToken, refreshToken, "company");

        // 응답 작성
        writeResponse(response, companyDetails.getCompany().getRole());
    }

    // 일반회원 로그인 성공에 따른 리프레시 토큰, 엑세스 토큰 발급
    private UserRefreshToken createUserRefreshTokenEntity(String email, String refreshToken) {
        UserRefreshToken existingRefreshToken = userRefreshTokenRepository.findByEmail(email).orElse(null);
        return UserRefreshToken.builder()
                .idx(existingRefreshToken != null ? existingRefreshToken.getIdx() : null)
                .refreshToken(refreshToken)
                .email(email)
                .build();
    }

    // 업체회원 로그인 성공에 따른 리프레시 토큰, 엑세스 토큰 발급
    private CompanyRefreshToken createCompanyRefreshTokenEntity(String email, String refreshToken) {
        CompanyRefreshToken existingRefreshToken = companyRefreshTokenRepository.findByEmail(email).orElse(null);
        return CompanyRefreshToken.builder()
                .idx(existingRefreshToken != null ? existingRefreshToken.getIdx() : null)
                .refreshToken(refreshToken)
                .email(email)
                .build();
    }

    //생성된 토큰과 유저를 구분할 3개의 토큰을 브라우저에 세팅
    private void createTokenCookies(HttpServletResponse response, String accessToken, String refreshToken, String type) {
        Cookie aToken = new Cookie("AToken", accessToken);
        aToken.setPath("/");
        aToken.setHttpOnly(true);
        aToken.setSecure(true);

        Cookie rToken = new Cookie("RToken", refreshToken);
        rToken.setPath("/");
        rToken.setHttpOnly(true);
        rToken.setSecure(true);
        rToken.setMaxAge(COOKIE_MAX_AGE);

        Cookie typeCookie = new Cookie("type", type);
        typeCookie.setPath("/");
        typeCookie.setSecure(true);
        typeCookie.setHttpOnly(true);

        response.addCookie(aToken);
        response.addCookie(rToken);
        response.addCookie(typeCookie);
    }

    private void writeResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("role", message);

        PrintWriter out = response.getWriter();
        BaseResponse<Map<String,Object>> baseResponse = new BaseResponse<>(responseData);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(baseResponse);
        out.print(jsonResponse);
    }

}
