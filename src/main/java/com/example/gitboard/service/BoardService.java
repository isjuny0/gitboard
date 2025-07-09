package com.example.gitboard.service;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.Board;
import com.example.gitboard.entity.User;
import com.example.gitboard.exception.BoardNotFoundException;
import com.example.gitboard.exception.UnauthorizedAccessException;
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

    private void checkAuthority(Board board, User user) {
        if (!board.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException();
        }
    }

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

    public BoardResponseDto update(Long id, BoardRequestDto requestDto, User user) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));

        checkAuthority(board, user); // 권한 체크

        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        return BoardMapper.toDto(boardRepository.save(board));
    }

    public void delete(Long id, User user) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException(id));

        checkAuthority(board, user); // 권한 체크

        boardRepository.delete(board);
    }

    public List<BoardResponseDto> findMyBoards(User user) {
        return boardRepository.findAllByUser(user).stream()
                .map(BoardMapper::toDto)
                .collect(Collectors.toList());
    }
}
