package org.example.backend.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.model.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.security.custom.model.dto.CustomOAuth2User;
import org.example.backend.global.security.jwt.JwtUtil;
import org.example.backend.global.security.jwt.model.entity.UserRefreshToken;
import org.example.backend.global.security.jwt.repository.UserRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final int COOKIE_MAX_AGE = 60 * 60 * 24 * 30;

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtUtil jwtUtil;

    // @Value("${domain}")
    // private String domain;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String name = URLEncoder.encode(oAuth2User.getName(), StandardCharsets.UTF_8.toString());
        String email = URLEncoder.encode(oAuth2User.getEmail(), StandardCharsets.UTF_8.toString());
        String type = URLEncoder.encode(oAuth2User.getRegistrationId(), StandardCharsets.UTF_8.toString());

        Optional<User> optionalUser = userRepository.findByEmail(oAuth2User.getEmail());
        if (optionalUser.isEmpty()){
            //가입된 적이 없으면 가입 기본정보 리턴
            getRedirectStrategy().sendRedirect(request, response, "/login/redirect?"+"isSuccess="+true+"&isExist="+false
                    +"&name="+name+"&email="+email+"&type="+type);
        }else {
            //받아온 아이디로 가입된적이 있으면 토큰 발급 후 리다이렉트
            User user = optionalUser.get();
            //oauth 로그인 타입이 일치하지 않으면 실패 (naver로그인 인데 google로 등록된 이메일일 경우)
            if (!user.getType().equals(type)){
                getRedirectStrategy().sendRedirect(request, response, "/login/redirect?"+"isSuccess="+false);
            }else {
                String refreshToken = jwtUtil.createRefreshToken(user.getIdx(), user.getEmail(), user.getRole());
                String accessToken = jwtUtil.createAccessToken(user.getIdx(), user.getEmail(), user.getRole());
                UserRefreshToken existingRefreshToken = userRefreshTokenRepository.findByEmail(user.getEmail()).orElse(null);
                UserRefreshToken reissuedUserRefreshToken = UserRefreshToken.builder()
                        .idx(existingRefreshToken != null ? existingRefreshToken.getIdx() : null)
                        .refreshToken(refreshToken)
                        .email(user.getEmail())
                        .build();
                userRefreshTokenRepository.save(reissuedUserRefreshToken);
                createTokenCookies(response, accessToken, refreshToken, "user");
                getRedirectStrategy().sendRedirect(request, response,
                        "/login/redirect?"+"isSuccess="+true+"&isExist="+true+"&role="+user.getRole());
            }
        }
    }

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

}
