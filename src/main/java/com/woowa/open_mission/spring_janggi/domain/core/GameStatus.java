package com.woowa.open_mission.spring_janggi.domain.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameStatus {

    WAITING("대기 중"),
    IN_PROGRESS("진행 중"),
    FINISHED("종료");

    private final String description;
}