package com.example.gitboard.repository;

import com.example.gitboard.entity.Board;
import com.example.gitboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByUser(User user);
}
