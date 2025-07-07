package com.example.gitboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // 예약어 피하기 위해 복수형 또는 다른 이름으로 지정
@Getter
@NoArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role;

    // 회원가입 전용 생성자
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "USER"; // 기본값
    }
}
