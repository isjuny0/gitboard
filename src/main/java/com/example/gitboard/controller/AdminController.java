package com.example.gitboard.controller;

import com.example.gitboard.repository.BoardRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final BoardRepository boardRepository;

    public AdminController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @DeleteMapping("/boards")
    public String deleteAllBoards(@AuthenticationPrincipal UserDetails userDetails) {
        // 관리자 확인은 Security 설정으로 이미 제한됨
        boardRepository.deleteAll();
        return "모든 게시글이 삭제되었습니다."
    }

}
