package org.example.backend.global.security.custom.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.global.security.custom.model.dto.CustomOAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2Service extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // kakao, naver 등...
        Map<String, Object> attributes = oAuth2User.getAttributes();

        return new CustomOAuth2User(registrationId, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes);
    }
}
