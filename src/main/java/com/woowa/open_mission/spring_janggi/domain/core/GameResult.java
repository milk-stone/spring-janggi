package com.woowa.open_mission.spring_janggi.domain.core;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public enum GameResult {

    CHO_WIN("초나라 승리", Team.CHO),
    HAN_WIN("한나라 승리", Team.HAN),
    DRAW("무승부", null);

    private final String description;
    private final Team winningTeam;

    public static GameResult from(Team winner) {
        if (winner == Team.CHO) {
            return CHO_WIN;
        }
        if (winner == Team.HAN) {
            return HAN_WIN;
        }
        return DRAW;
    }
}
