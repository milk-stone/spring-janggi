package com.woowa.open_mission.spring_janggi.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // * Global
    BLANK_INPUT(HttpStatus.BAD_REQUEST, "GE_001", "빈 입력은 허용되지 않습니다."),
    // * Member
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "ME_001", "이메일로 유효하지 않은 값이 입력되었습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ME_002", "비밀번호로 유효하지 않은 값이 입력되었습니다."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "ME_003", "닉네임으로 유효하지 않은 값이 입력되었습니다."),
    // * Game
    INVALID_GAME_STATUS(HttpStatus.BAD_REQUEST, "GE_001", "게임이 진행 중인 상태가 아닙니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GE_002", "해당 게임이 존재하지 않습니다."),
    // * Position
    INVALID_POSITION_FORMAT(HttpStatus.BAD_REQUEST, "PE_001", "유효하지 않은 좌표 포맷입니다."),
    INVALID_ROW_FORMAT(HttpStatus.BAD_REQUEST, "PE_002", "좌표의 행(Row)은 숫자여야 합니다."),
    POSITION_X_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "PE_003", "X 좌표가 장기판 범위를 벗어났습니다."),
    POSITION_Y_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "PE_004", "Y 좌표가 장기판 범위를 벗어났습니다."),
    // * Piece
    PIECE_PARAMETER_REQUIRED(HttpStatus.BAD_REQUEST, "PE_001", "팀과 기물 종류는 필수입니다."),
    private final HttpStatus status;
    private final String code;
    private final String message;
}
