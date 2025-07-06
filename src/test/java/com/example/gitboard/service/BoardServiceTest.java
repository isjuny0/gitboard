package com.example.gitboard.service;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.Board;
import com.example.gitboard.exception.BoardNotFoundException;
import com.example.gitboard.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    void setUp() {
        org.mockito.MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createBoard() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("테스트 제목");
        requestDto.setContent("테스트 내용");

        Board mockBoard = new Board();
        mockBoard.setId(1L);
        mockBoard.setTitle(requestDto.getTitle());
        mockBoard.setContent(requestDto.getContent());

        when(boardRepository.save(any(Board.class))).thenReturn(mockBoard);

        // when
        BoardResponseDto responseDto = boardService.create(requestDto);

        // then
        assertThat(responseDto.getId()).isEqualTo(1L);
        assertThat(responseDto.getTitle()).isEqualTo("테스트 제목");
        assertThat(responseDto.getContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("게시글 단건 조회 성공")
    void findById_success() {
        // given
        Board board = new Board();
        board.setId(1L);
        board.setTitle("제목");
        board.setContent("내용");

        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        // when
        BoardResponseDto result = boardService.findById(1L);

        // when
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("게시글 단건 조회 실패 - 존재하지 않는 ID")
    void findById_fail() {
        // given
        when(boardRepository.findById(99L)).thenReturn(Optional.empty());

        // expect
        assertThrows(BoardNotFoundException.class, () -> boardService.findById(99L));
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateBoard_success() {
        // given
        Long boardId = 1L;
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("수정된 제목");
        requestDto.setContent("수정된 내용");

        Board originalBoard = new Board();
        originalBoard.setId(boardId);
        originalBoard.setTitle("기존 제목");
        originalBoard.setContent("기존 내용");

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(originalBoard));
        when(boardRepository.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        BoardResponseDto responseDto = boardService.update(boardId, requestDto);

        // then
        assertThat(responseDto.getTitle()).isEqualTo("수정된 제목");
        assertThat(responseDto.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 ID")
    void updateBoard_fail() {
        // given
        Long invalidId = 99L;
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("수정 제목");
        requestDto.setContent("수정 내용");

        when(boardRepository.findById(invalidId)).thenReturn(Optional.empty());

        // expect
        assertThrows(BoardNotFoundException.class, () -> boardService.update(invalidId, requestDto));
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deleteBoard_success() {
        // given
        Long boardId = 1L;
        when(boardRepository.existsById(boardId)).thenReturn(true);
        doNothing().when(boardRepository).deleteById(boardId);

        // when
        boardService.delete(boardId);

        // then
        verify(boardRepository, times(1)).deleteById(boardId);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 ID")
    void deleteBoard_fail() {
        // given
        Long invalidId = 999L;
        when(boardRepository.existsById(invalidId)).thenReturn(false);

        // expect
        assertThrows(BoardNotFoundException.class, () -> boardService.delete(invalidId));
    }
}
