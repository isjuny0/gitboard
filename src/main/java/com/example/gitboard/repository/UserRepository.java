package com.example.gitboard.repository;

import com.example.gitboard.dto.UserResponseDto;
import com.example.gitboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
