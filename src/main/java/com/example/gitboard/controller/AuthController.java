package com.example.gitboard.controller;

import com.example.gitboard.dto.*;
import com.example.gitboard.entity.RefreshToken;
import com.example.gitboard.entity.User;
import com.example.gitboard.repository.RefreshTokenRepository;
import com.example.gitboard.repository.UserRepository;
import com.example.gitboard.security.JwtUtil;
import com.example.gitboard.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        String role = "ROLE_" + requestDto.getRole().toUpperCase(); // ROLE_USER or ROLE_ADMIN

        User user = new User(requestDto.getUsername(), encodedPassword, role);
        userRepository.save(user);
        return "회원가입 완료";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        String accessToken = jwtUtil.createToken(user.getUsername());
        String refreshToken = jwtUtil.createRefreshToken(user.getUsername());

        // 🔄 기존 토큰 존재 시 업데이트
        RefreshToken savedToken = refreshTokenRepository.findById(user.getUsername())
                .map(entity -> {
                    entity.updateToken(refreshToken);
                    return entity;
                })
                .orElse(new RefreshToken(user.getUsername(), refreshToken));

        refreshTokenRepository.save(savedToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody @Valid RefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().build();
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        RefreshToken stored = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("저장된 토큰 없음"));

        if (!stored.getToken().equals(refreshToken)) {
            return ResponseEntity.status(403).build(); // 불일치
        }

        String newAccessToken = jwtUtil.createToken(username);
        return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshRequestDto requestDto) {
        String username = jwtUtil.getUsernameFromToken(requestDto.getRefreshToken());
        refreshTokenRepository.deleteById(username);
        return ResponseEntity.ok().build();
    }
}
