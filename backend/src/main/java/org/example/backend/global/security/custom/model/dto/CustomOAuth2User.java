package org.example.backend.global.security.custom.model.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {


    // OAuth2 공급자 ID (예: google, kakao, naver)
    private final String registrationId;
    // OAuth2 공급자가 제공한 사용자 정보 맵
    // 사용자 정보를 문자열로 주는게 아닌 Map에 변수명:값 형태로 담아서 전달받음
    private final Map<String, Object> attributes;
    // Spring Security에서 사용하는 사용자 권한 목록
    private final Collection<GrantedAuthority> authorities;
    // 사용자 이름 (OAuth2 공급자로부터 추출)
    private String name;
    // 사용자 이메일 (OAuth2 공급자로부터 추출)
    private String email;

    public CustomOAuth2User(String registrationId, Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes) {
        this.attributes = attributes;
        this.authorities = (Collection<GrantedAuthority>) authorities;
        this.registrationId = registrationId;

        // OAuth2 인증 공급자에 따라 변수값 매핑
        // 공급자마다 가져올 변수명이 조금씩 다름

        // 카카오
        if (this.registrationId.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) this.attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            this.name = (String) profile.get("nickname");
            this.email = (String) kakaoAccount.get("email");
        }
        // 네이버
        if (this.registrationId.equals("naver")) {
            Map<String, Object> naverAccount = (Map<String, Object>) this.attributes.get("response");
            this.name = (String) naverAccount.get("name");
            this.email = (String) naverAccount.get("email");
        }
        // 구글
        if (this.registrationId.equals("google")) {
            this.name = (String) this.attributes.get("name");
            this.email = (String) this.attributes.get("email");
        }

    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
