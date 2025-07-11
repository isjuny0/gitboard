package com.example.gitboard.controller;

import com.example.gitboard.dto.UserResponseDto;
import com.example.gitboard.entity.User;
import com.example.gitboard.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SecurityRequirement(name = "basicAuth")
@Tag(name = "회원 API", description = "회원 관련 기능 제공")
@RestController
@RequiredArgsConstructor
public class UserController {

    @Operation(summary = "내 프로필 조회", description = "로그인된 사용자의 정보를 반환합니다.")
    @GetMapping("/profile")
    public UserResponseDto getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new UserResponseDto(user);
    }
}
