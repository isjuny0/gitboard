package com.example.gitboard.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String username;

    private String token;

    private LocalDateTime expiresAt;

    public void updateToken(String newToken, LocalDateTime newExpiresAt) {
        this.token = token;
        this.expiresAt = newExpiresAt;
    }
}
