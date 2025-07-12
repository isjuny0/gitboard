package com.example.gitboard.security;

import com.example.gitboard.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper; // JSON 직렬화를 위해 ObjectMapper 사용

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 방어적 검사: 이메일이 null이 아닌지 확인
        if (email == null) {
            // 이메일이 예상되지만 누락된 경우 오류를 로깅하거나 특정 예외를 throw합니다.
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 Principal에서 이메일을 찾을 수 없습니다.");
            return;
        }

        // JWT 생성
        String accessToken = jwtUtil.createToken(email);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // JSON 응답 준비
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), tokens); // ObjectMapper를 사용하여 JSON 작성
    }
}