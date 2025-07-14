package com.example.gitboard.security;

import com.example.gitboard.entity.RefreshToken;
import com.example.gitboard.entity.User;
import com.example.gitboard.repository.RefreshTokenRepository;
import com.example.gitboard.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String FRONTEND_REDIRECT_URI = "http://localhost:8080/test.html";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "OAuth2 사용자 이메일이 없습니다.");
            return;
        }

        User user = userRepository.findByUsername(email)
                .orElseGet(() -> userRepository.saveAndFlush(new User(email, "", "ROLE_USER")));

        // JWT 발급
        String accessToken = jwtUtil.createToken(email);
        String refreshToken = jwtUtil.createRefreshToken(email);

        LocalDateTime expiresAt = jwtUtil.getExpirationFromToken(refreshToken).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // 🔄 RefreshToken 저장 or 갱신
        refreshTokenRepository.findById(email)
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken, expiresAt),
                        () -> refreshTokenRepository.save(new RefreshToken(email, refreshToken, expiresAt))
                );

        // 리디렉션 URL
        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT_URI)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        response.sendRedirect(targetUrl);
    }
}