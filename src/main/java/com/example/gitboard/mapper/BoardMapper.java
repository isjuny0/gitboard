package com.example.gitboard.mapper;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.Board;
import com.example.gitboard.entity.User;

public class BoardMapper {

    public static Board toEntity(BoardRequestDto dto, User user) {
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setUser(user);
        return board;
    }

    public static BoardResponseDto toDto(Board board) {
        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getUser().getUsername() // 연관관계에서 가져오기
        );
    }
}
