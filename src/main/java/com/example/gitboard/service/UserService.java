package com.example.gitboard.service;

import com.example.gitboard.dto.UserResponseDto;
import com.example.gitboard.dto.UserUpdateRequestDto;
import com.example.gitboard.entity.User;
import com.example.gitboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public List<UserResponseDto> findAllUsers() {
        List<UserResponseDto> users = userRepository.findAll().stream().map(UserResponseDto::new).collect(Collectors.toList());
        return users;
    }

    public UserResponseDto findUserById(Long id) {
        return userRepository.findById(id).map(UserResponseDto::new).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("일치하는 사용자가 없습니다.");
        }
        userRepository.deleteById(id);
    }

    public void updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setRole("ROLE_" + role.toUpperCase());
        userRepository.save(user);
    }
}
