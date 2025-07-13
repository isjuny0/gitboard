package com.example.gitboard.security;

import com.example.gitboard.entity.User;
import com.example.gitboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("UserDetailsServiceImpl: 사용자명으로 조회 시도: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.err.println("UserDetailsServiceImpl: 사용자 " + username + "을(를) 데이터베이스에서 찾을 수 없습니다.");
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });
        System.out.println("UserDetailsServiceImpl: 사용자 " + user.getUsername() + " 성공적으로 로드.");
        return new UserDetailsImpl(user);
    }
}