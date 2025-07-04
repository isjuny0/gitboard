package com.example.gitboard.entitiy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Board {
    @Id @GeneratedValue
    private long id;
    private String title;
    private String content;
}