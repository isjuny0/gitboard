package com.example.gitboard.controller;

import com.example.gitboard.dto.SignupRequestDto;
import com.example.gitboard.dto.UserProfileDto;
import com.example.gitboard.entity.User;
import com.example.gitboard.repository.UserRepository;
import com.example.gitboard.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public String signup(@RequestBody @Valid SignupRequestDto requestDto) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        String role = "ROLE_" + requestDto.getRole().toUpperCase(); // ROLE_USER or ROLE_ADMIN

        User user = new User(requestDto.getUsername(), encodedPassword, role);
        userRepository.save(user);
        return "회원가입 완료";
    }

    @GetMapping("/users/me")
    public UserProfileDto getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new UserProfileDto(user.getId(), user.getUsername(), user.getRole());
    }

}
