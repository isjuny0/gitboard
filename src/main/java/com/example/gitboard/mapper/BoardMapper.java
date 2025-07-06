package com.example.gitboard.mapper;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.Board;

public class BoardMapper {

    public static Board toEntity(BoardRequestDto dto) {
        Board board = new Board();
        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        return board;
    }

    public static BoardResponseDto toDto(Board board) {
        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getContent()
        );
    }
}
