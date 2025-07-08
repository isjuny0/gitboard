package com.example.gitboard.service;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.Board;
import com.example.gitboard.entity.User;
import com.example.gitboard.exception.BoardNotFoundException;
import com.example.gitboard.mapper.BoardMapper;
import com.example.gitboard.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardResponseDto create(BoardRequestDto requestDto, User user) {
        Board board = BoardMapper.toEntity(requestDto, user);
        return BoardMapper.toDto(boardRepository.save(board));
    }

    public List<BoardResponseDto> findAll() {
        return boardRepository.findAll().stream()
                .map(BoardMapper::toDto)
                .collect(Collectors.toList());
    }

    public BoardResponseDto findById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));
        return BoardMapper.toDto(board);
    }

    public BoardResponseDto update(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        return BoardMapper.toDto(boardRepository.save(board));
    }

    public void delete(Long id) {
        if (!boardRepository.existsById(id)) {
            throw new BoardNotFoundException(id);
        }
        boardRepository.deleteById(id);
    }
}
