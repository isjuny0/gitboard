package com.example.gitboard.integration;

import com.example.gitboard.dto.BoardRequestDto;
import com.example.gitboard.dto.BoardResponseDto;
import com.example.gitboard.entity.Board;
import com.example.gitboard.repository.BoardRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 생성 후 전체 목록 조회 통합 테스트")
    void createAndReadBoard() {
        // given
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("통합 테스트 제목");
        requestDto.setContent("통합 테스트 내용");

        String url = "http://localhost:" + port + "/boards";

        // when: 게시글 등록
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardRequestDto> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<Board> response = restTemplate.postForEntity(url, request, Board.class);

        // then: 등록된 게시글 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo(requestDto.getTitle());

        // and: 전체 게시글 목록 조회
        ResponseEntity<Board[]> listResponse = restTemplate.getForEntity(url, Board[].class);
        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(List.of(listResponse.getBody()))
                .extracting("title")
                .contains("통합 테스트 제목");

    }

    @Test
    @DisplayName("게시글 단건 조회")
    void getBoardById() {
        // given
        Board saved = boardRepository.save(new Board(null, "조회 제목", "조회 내용"));

        String url = "http://localhost:" + port + "/boards/" + saved.getId();

        // when
        ResponseEntity<Board> response = restTemplate.getForEntity(url, Board.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("조회 제목");
    }

    @Test
    @DisplayName("게시글 수정 통합 테스트")
    void updateBoard() {
        // given
        Board saved = boardRepository.save(new Board(null, "원래 제목", "원래 내용"));
        BoardRequestDto requestDto = new BoardRequestDto();
        requestDto.setTitle("수정된 제목");
        requestDto.setContent("수정된 내용");

        String url = "http://localhost:" + port + "/boards/" + saved.getId();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardRequestDto> request = new HttpEntity<>(requestDto, headers);

        // when
        ResponseEntity<Board> response = restTemplate.exchange(url, HttpMethod.PUT, request, Board.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("게시글 삭제 통합 테스트")
    void deleteBoard() {
        // given
        Board saved = boardRepository.save(new Board(null, "삭제 제목", "삭제 내용"));

        String url = "http://localhost:" + port + "/boards/" + saved.getId();

        // when
        restTemplate.delete(url);

        // then
        boolean exists = boardRepository.existsById(saved.getId());
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 404 반환")
    void getNonExistBoard() {
        // given
        Long invalidId = 9999L;
        String url = "http://localhost:" + port + "/boards/" + invalidId;

        // when
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("제목/내용 누락 시 유효성 검사 실패")
    void createBoardValidationFail() {
        BoardRequestDto invalid = new BoardRequestDto(); // title, content null

        String url = "http://localhost:" + port + "/boards";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardRequestDto> request = new HttpEntity<>(invalid, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("제목은 필수입니다.");
        assertThat(response.getBody()).contains("내용은 필수입니다.");
    }

    @Test
    @DisplayName("게시글 등록 시 응답 DTO 검증")
    void createBoard_responseDtoCheck() {
        // given
        BoardRequestDto dto = new BoardRequestDto();
        dto.setTitle("DTO 테스트 제목");
        dto.setContent("DTO 테스트 내용");

        String url = "http://localhost:" + port + "/boards";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BoardRequestDto> request = new HttpEntity<>(dto, headers);

        // when
        ResponseEntity<BoardResponseDto> response = restTemplate.postForEntity(
                url, request, BoardResponseDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        BoardResponseDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getTitle()).isEqualTo("DTO 테스트 제목");
        assertThat(body.getContent()).isEqualTo("DTO 테스트 내용");
    }

    @Test
    @DisplayName("전체 게시글 조회 시 응답 DTO 배열 확인")
    void getBoards_responseDtoListCheck() {
        boardRepository.save(new Board(null, "제목1", "내용1"));
        boardRepository.save(new Board(null, "제목2", "내용2"));

        String url = "http://localhost:" + port + "/boards";

        ResponseEntity<BoardResponseDto[]> response = restTemplate.getForEntity(url, BoardResponseDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()[0].getTitle()).isEqualTo("제목1");
    }
}
