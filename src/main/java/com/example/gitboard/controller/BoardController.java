package com.example.gitboard.controller;

import com.example.gitboard.entitiy.Board;
import com.example.gitboard.exception.BoardNotFoundException;
import com.example.gitboard.repository.BoardRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판 API", description = "게시글 등록, 조회, 삭제 등 처리")
@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardRepository boardRepository;

    @Operation(summary = "게시글 등록", description = "새로운 게시글을 등록합니다.")
    @PostMapping("/boards")
    public Board createBoard(@RequestBody Board board) {
        return boardRepository.save(board);
    }

    @Operation(summary = "게시글 목록 조회", description = "전체 게시글을 조회합니다.")
    @GetMapping("/boards")
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/boards/{id}")
    public void deleteBoard(@PathVariable Long id) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException(id);
        }
        boardRepository.deleteById(id);
    }

    @Operation(summary = "게시글 업데이트", description = "게시글을 업데이트합니다.")
    @PutMapping("/boards/{id}")
    public Board updateBoard(@PathVariable Long id, @RequestBody Board board) {
        return boardRepository.save(board);
    }

    @Operation(summary = "단일 게시글 조회", description = "단일 게시글을 조회합니다.")
    @GetMapping("/boards/{id}")
    public Board getBoard(@PathVariable Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));
    }
}
