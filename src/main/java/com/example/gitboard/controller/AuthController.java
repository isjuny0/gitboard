package com.example.gitboard.controller;

import com.example.gitboard.dto.*;
import com.example.gitboard.entity.RefreshToken;
import com.example.gitboard.entity.User;
import com.example.gitboard.repository.RefreshTokenRepository;
import com.example.gitboard.repository.UserRepository;
import com.example.gitboard.security.JwtUtil;
import com.example.gitboard.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

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

        LocalDateTime expiresAt = jwtUtil.getExpirationFromToken(refreshToken).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        RefreshToken savedToken = refreshTokenRepository.findById(user.getUsername())
                .map(entity -> {
                    entity.updateToken(refreshToken, expiresAt); // 🔄 기존 토큰 갱신
                    return entity;
                })
                .orElse(new RefreshToken(user.getUsername(), refreshToken, expiresAt));

        refreshTokenRepository.save(savedToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(@RequestBody @Valid RefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body(null);
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        RefreshToken stored = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰이 없습니다. 다시 로그인해주세요."));

        if (!stored.getToken().equals(refreshToken)) {
            return ResponseEntity.status(403).body(null);
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(username);
            return ResponseEntity.status(401).body(null); // 만료됨
        }

        String newAccessToken = jwtUtil.createToken(username);
        String newRefreshToken = jwtUtil.createRefreshToken(username);
        LocalDateTime newExpiresAt = jwtUtil.getExpirationFromToken(newRefreshToken).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        stored.updateToken(newRefreshToken, newExpiresAt);
        refreshTokenRepository.save(stored);

        return ResponseEntity.ok(new LoginResponseDto(newAccessToken, newRefreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody @Valid RefreshRequestDto requestDto) {
        String refreshToken = requestDto.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰입니다.");
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        refreshTokenRepository.deleteById(username);

        return ResponseEntity.ok("로그아웃 완료");
    }
}