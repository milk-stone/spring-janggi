package com.woowa.open_mission.spring_janggi.domain.entity;

import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String email;
    private String password;
    private String nickname;

    public Member(String email, String password, String nickname) {
        validateEmail(email);
        this.email = email;
        validatePassword(password);
        this.password = password;
        validateNickname(nickname);
        this.nickname = nickname;
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.BLANK_INPUT);
        }
        if (!email.contains("@")) {
            throw new BusinessException(ErrorCode.INVALID_EMAIL);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new BusinessException(ErrorCode.BLANK_INPUT);
        }
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new BusinessException(ErrorCode.BLANK_INPUT);
        }
        if (nickname.length() < 2 || nickname.length() > 10) {
            throw new BusinessException(ErrorCode.INVALID_NICKNAME);
        }
    }
}
