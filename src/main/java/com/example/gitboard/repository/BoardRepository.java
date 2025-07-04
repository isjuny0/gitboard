package com.example.gitboard.repository;

import com.example.gitboard.entitiy.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
