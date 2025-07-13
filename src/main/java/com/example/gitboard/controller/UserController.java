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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "회원 API", description = "회원 관련 기능 제공")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 프로필 조회", description = "로그인된 사용자의 정보를 반환합니다.")
    @GetMapping("/me")
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

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자가 본인의 계정을 삭제합니다.")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
