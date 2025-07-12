package com.example.gitboard.service;

import com.example.gitboard.entity.User;
import com.example.gitboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(request);
        String email = oauthUser.getAttribute("email");

        // 이메일이 null인 경우 예외를 던지거나 처리하는 것을 고려하세요. 이메일은 중요한 정보입니다.
        if (email == null) {
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 이메일을 찾을 수 없습니다.");
        }

        // 사용자 찾기 또는 생성
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User(email, "", "ROLE_USER"); // OAuth2 사용자의 경우 비밀번호는 비어 있을 수 있습니다.
                    return userRepository.save(newUser);
                });

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                oauthUser.getAttributes(),
                "email"
        );
    }
}