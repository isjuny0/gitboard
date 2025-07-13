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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional // 이 메서드 전체를 하나의 트랜잭션으로 묶습니다.
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = new DefaultOAuth2UserService().loadUser(request);
        String email = oauthUser.getAttribute("email");

        if (email == null) {
            System.err.println("CustomOAuth2UserService: OAuth2 제공자로부터 이메일을 받지 못했습니다.");
            throw new OAuth2AuthenticationException("OAuth2 제공자로부터 이메일을 찾을 수 없습니다.");
        }
        System.out.println("CustomOAuth2UserService: 구글에서 받은 이메일: " + email);

        // 사용자 찾기
        User user = userRepository.findByUsername(email)
                .orElse(null);

        if (user == null) {
            System.err.println("CustomOAuth2UserService: 사용자 " + email + "이 DB에 없습니다.");
            throw new OAuth2AuthenticationException("OAuth2 사용자 정보가 DB에 존재하지 않습니다.");
        }
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                oauthUser.getAttributes(),
                "email"
        );
    }
}