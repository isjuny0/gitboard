package com.example.gitboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthSuccessController {

    @GetMapping("/oauth-success")
    public String oauthSuccess() {
        return "OAuth2 로그인 성공!"; // 혹은 프론트엔드 페이지로 redirect
    }
}
