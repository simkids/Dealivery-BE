package org.example.backend.global.security.custom.model.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final String registrationId;
    private final Map<String, Object> attributes;
    private final Collection<GrantedAuthority> authorities;
    private String name;
    private String email;

    public CustomOAuth2User(String registrationId, Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes) {
        this.attributes = attributes;
        this.authorities = (Collection<GrantedAuthority>) authorities;
        this.registrationId = registrationId;
        if (this.registrationId.equals("kakao")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) this.attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            this.name = (String) profile.get("nickname");
            this.email = (String) kakaoAccount.get("email");
        }
        if (this.registrationId.equals("naver")) {
            Map<String, Object> naverAccount = (Map<String, Object>) this.attributes.get("response");
            this.name = (String) naverAccount.get("name");
            this.email = (String) naverAccount.get("email");
        }
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
