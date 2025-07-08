package com.example.gitboard.controller;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.exception.BoardNotFoundException;
import com.example.gitboard.service.BoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /boards - 게시글 등록 성공")
    void createBoard_success() throws Exception {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("테스트 제목");
        requestDto.setContent("테스트 내용");

        BoardResponseDto responseDto = new BoardResponseDto(1L, "테스트 제목", "테스트 내용");
        when(boardService.create(any(BoardRequestDto.class))).thenReturn(responseDto);

        // when + then
        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"));
    }

    @Test
    @DisplayName("POST /boards - 유효성 검사 실패")
    void craeteBoard_validationFail() throws Exception {
        // given
        BoardRequestDto invalidDto = new BoardRequestDto(); //title, content 모두 null

        // when + then
        mockMvc.perform(post("/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists())
                .andExpect(jsonPath("$.errors.content").exists());
    }

    @Test
    @DisplayName("GET /boards/{id} - 게시글 단건 조회 성공")
    void getBoard_success() throws Exception {
        Long boardId = 1L;
        BoardResponseDto responseDto = new BoardResponseDto(boardId, "제목", "내용");
        when(boardService.findById(boardId)).thenReturn(responseDto);

        mockMvc.perform(get("/boards/{id}", boardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(boardId))
                .andExpect(jsonPath("$.title").value("제목"))
                .andExpect(jsonPath("$.content").value("내용"));
    }

    @Test
    @DisplayName("GET /boards/{id} - 존재하지 않는 게시글")
    void getBoard_notFound() throws Exception {
        Long invalidId = 999L;
        when(boardService.findById(invalidId)).thenThrow(new BoardNotFoundException(invalidId));

        mockMvc.perform(get("/boards/{id}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다: " + invalidId));
    }

    @Test
    @DisplayName("PUT /boards/{id} - 게시글 수정 성공")
    void updateBoard_success() throws Exception {
        Long boardId = 1L;
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("수정된 제목");
        requestDto.setContent("수정된 내용");

        BoardResponseDto responseDto = new BoardResponseDto(boardId, "수정된 제목", "수정된 내용");
        when(boardService.update(eq(boardId), any(BoardRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/boards/{id}", boardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("PUT /boards/{id} - 존재하지 않는 게시글 수정 시도")
    void updateBoard_notFound() throws Exception {
        Long invalidId = 999L;
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("수정된 제목");
        requestDto.setContent("수정된 내용");

        when(boardService.update(eq(invalidId), any(BoardRequestDto.class)))
                .thenThrow(new BoardNotFoundException(invalidId));

        mockMvc.perform(put("/boards/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다: " + invalidId));
    }

    @Test
    @DisplayName("DELETE /boards/{id} - 게시글 삭제 성공")
    void deleteBoard_success() throws Exception {
        Long boardId = 1L;
        doNothing().when(boardService).delete(boardId);

        mockMvc.perform(delete("/boards/{id}", boardId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /boards/{id} - 존재하지 않는 게시글 삭제 시도")
    void deleteBoard_notFound() throws Exception {
        Long invalidId = 999L;
        doThrow(new BoardNotFoundException(invalidId)).when(boardService).delete(invalidId);

        mockMvc.perform(delete("/boards/{id}", invalidId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("게시글을 찾을 수 없습니다: " + invalidId));
    }

    @Test
    @DisplayName("GET /boards - 전체 게시글 조회")
    void getAllBoards() throws Exception {
        // given
        List<BoardResponseDto> boardList = List.of(
                new BoardResponseDto(1L, "제목 1", "내용 1"),
                new BoardResponseDto(2L, "제목 2", "내용 2")
        );

        when(boardService.findAll()).thenReturn(boardList);

        // when + then
        mockMvc.perform(get("/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("제목 1"))
                .andExpect(jsonPath("$[0].content").value("내용 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("제목 2"))
                .andExpect(jsonPath("$[1].content").value("내용 2"));
    }
}
