package com.example.gitboard.service;

import com.example.gitboard.dto.UserUpdateRequestDto;
import com.example.gitboard.entity.User;
import com.example.gitboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void updatePassword(User user, UserUpdateRequestDto userUpdateRequestDto) {
        if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(userUpdateRequestDto.getNewPassword()));
        userRepository.save(user);
    }
}
