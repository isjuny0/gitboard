package com.example.gitboard.controller;

import com.example.gitboard.dto.LoginRequestDto;
import com.example.gitboard.dto.LoginResponseDto;
import com.example.gitboard.dto.SignupRequestDto;
import com.example.gitboard.entity.User;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

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
        User user = userDetails.getUser(); // ⬅️ 수정된 부분
        String token = jwtUtil.createToken(user.getUsername());

        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
