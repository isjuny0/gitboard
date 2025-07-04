package com.example.gitboard.controller;

import com.example.gitboard.entitiy.Board;
import com.example.gitboard.repository.BoardRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardController {
    private final BoardRepository boardRepository;

    public BoardController(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @PostMapping("/boards")
    public Board createBoard(@RequestBody Board board) {
        return boardRepository.save(board);
    }

    @GetMapping("/boards")
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @DeleteMapping("/boards/{id}")
    public void deleteBoard(@PathVariable Long id) {
        boardRepository.deleteById(id);
    }

    @PutMapping("/boards/{id}")
    public Board updateBoard(@PathVariable Long id, @RequestBody Board board) {
        return boardRepository.save(board);
    }
}
