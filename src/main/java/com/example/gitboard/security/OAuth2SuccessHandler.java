package com.example.gitboard.security;

import com.example.gitboard.entity.User;
import com.example.gitboard.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private static final String FRONTEND_REDIRECT_URI = "http://localhost:8080/test.html";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            System.err.println("OAuth2SuccessHandler: OAuth2 Principal에서 이메일을 찾을 수 없습니다.");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 Principal에서 이메일을 찾을 수 없습니다.");
            return;
        }

        // 🟡 사용자 저장 책임을 이곳으로 이동
        User user = userRepository.findByUsername(email)
                .orElseGet(() -> {
                    User newUser = new User(email, "", "ROLE_USER");
                    User savedUser = userRepository.saveAndFlush(newUser); // 즉시 DB 반영
                    System.out.println("OAuth2SuccessHandler: 사용자 새로 저장됨 → ID: " + savedUser.getId());
                    return savedUser;
                });

        System.out.println("OAuth2SuccessHandler: JWT 발급을 위한 이메일: " + email);

        String accessToken = jwtUtil.createToken(email);
        String refreshToken = jwtUtil.createRefreshToken(email);

        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT_URI)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        System.out.println("OAuth2SuccessHandler: 프론트엔드 리다이렉트 URL: " + targetUrl);
        response.sendRedirect(targetUrl);
    }
}