package com.example.gitboard.controller;

import com.example.gitboard.dto.RoleUpdateRequestDto;
import com.example.gitboard.dto.UserResponseDto;
import com.example.gitboard.repository.BoardRepository;
import com.example.gitboard.security.UserDetailsImpl;
import com.example.gitboard.service.BoardService;
import com.example.gitboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Secured("ROLE_ADMIN")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final BoardService boardService;
    private final UserService userService;

    @DeleteMapping("/boards")
    public String deleteAllBoards() {
        // 관리자 확인은 Security 설정으로 이미 제한됨
        boardService.deleteAllBoards();
        return "모든 게시글이 삭제되었습니다.";
    }

    @GetMapping("/users")
    public List<UserResponseDto> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserResponseDto findUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PutMapping("/users/{id}/role")
    public void updateUserRole(@PathVariable Long id, @RequestBody RoleUpdateRequestDto roleUpdateRequestDto) {
        userService.updateUserRole(id, roleUpdateRequestDto.getRole());
    }
}
