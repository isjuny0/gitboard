package com.example.gitboard.dto;

import com.example.gitboard.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;
    private String username;
    private String role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}
