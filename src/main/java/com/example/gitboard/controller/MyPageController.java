package com.example.gitboard.controller;

import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.User;
import com.example.gitboard.security.UserDetailsImpl;
import com.example.gitboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("my")
public class MyPageController {

    private final BoardService boardService;

    @GetMapping("/profile")
    public User getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUser();
    }

    @GetMapping("/posts")
    public List<BoardResponseDto> getMyPosts(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.findByUser(userDetails.getUser());
    }
}
