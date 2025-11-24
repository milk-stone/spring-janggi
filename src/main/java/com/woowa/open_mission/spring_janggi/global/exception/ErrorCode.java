package com.woowa.open_mission.spring_janggi.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "ME_004", "이미 존재하는 이메일입니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "ME_005", "이메일 혹은 비밀번호가 일치하지 않습니다."),
    // * Game
    INVALID_GAME_STATUS(HttpStatus.BAD_REQUEST, "GE_001", "게임이 진행 중인 상태가 아닙니다."),
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "GE_002", "해당 게임이 존재하지 않습니다."),
    INVALID_STATUS_NOT_WAITING(HttpStatus.BAD_REQUEST, "GE_003", "이미 시작되었거나 종료된 게임입니다."),
    // * Position
    INVALID_POSITION_FORMAT(HttpStatus.BAD_REQUEST, "PE_001", "유효하지 않은 좌표 포맷입니다."),
    INVALID_ROW_FORMAT(HttpStatus.BAD_REQUEST, "PE_002", "좌표의 행(Row)은 숫자여야 합니다."),
    POSITION_X_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "PE_003", "X 좌표가 장기판 범위를 벗어났습니다."),
    POSITION_Y_OUT_OF_BOUNDS(HttpStatus.BAD_REQUEST, "PE_004", "Y 좌표가 장기판 범위를 벗어났습니다."),
    // * Piece
    PIECE_PARAMETER_REQUIRED(HttpStatus.BAD_REQUEST, "PE_001", "팀과 기물 종류는 필수입니다."),
    // * Board
    PIECE_NOT_FOUND(HttpStatus.BAD_REQUEST, "BE_001", "해당 위치에 기물이 없습니다."),
    IS_NOT_YOUR_TURN(HttpStatus.BAD_REQUEST, "BE_002", "자신의 기물만 움직일 수 있습니다."),
    CANNOT_MOVE_TO_SAME_POSITION(HttpStatus.BAD_REQUEST, "BE_003", "제자리로 이동할 수 없습니다."),
    CANNOT_CAPTURE_SAME_TEAM(HttpStatus.BAD_REQUEST, "BE_004", "아군은 잡을 수 없습니다."),
    INVALID_MOVE_RULE(HttpStatus.BAD_REQUEST, "BE_005", "해당 기물의 이동 규칙에 어긋납니다."),
    KING_IS_IN_DANGER(HttpStatus.BAD_REQUEST, "BE_006", "왕이 위험합니다. 장군을 막아야 합니다!"),
    GAME_DATA_ERROR(HttpStatus.BAD_REQUEST, "BE_007", "게임 데이터 에러입니다."),
    // * System
    JSON_CONVERSION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SYS_001", "Json으로 변환 도중 혹은 Json을 변환하는 도중 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
    }
