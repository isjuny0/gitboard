package com.example.gitboard.service;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.entity.Board;
import com.example.gitboard.exception.BoardNotFoundException;
import com.example.gitboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public Board create(BoardRequestDto requestDto) {
        Board board = new Board();
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        return boardRepository.save(board);
    }

    public List<Board> findAll() {
        return boardRepository.findAll();
    }

    public Board findById(Long id) {
        return boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));
    }

    public Board update(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        return boardRepository.save(board);
    }

    public void delete(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException(id);
        }
        boardRepository.deleteById(id);
    }
}
