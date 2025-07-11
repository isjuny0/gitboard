package com.example.gitboard.controller;

import com.example.gitboard.dto.UserResponseDto;
import com.example.gitboard.dto.UserUpdateRequestDto;
import com.example.gitboard.entity.User;
import com.example.gitboard.security.UserDetailsImpl;
import com.example.gitboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "회원 API", description = "회원 관련 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "로그인된 사용자의 정보를 반환합니다.")
    @GetMapping
    public UserResponseDto getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return new UserResponseDto(user);
    }

    @Operation(summary = "비밀번호 변경", description = "회원의 비밀번호를 변경합니다.")
    @PutMapping("/password")
    public void updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                               @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto) {
        userService.updatePassword(userDetails.getUser(), userUpdateRequestDto);
    }
}
