package com.woowa.open_mission.spring_janggi.controller.dto;

import com.woowa.open_mission.spring_janggi.domain.core.GameStatus;
import com.woowa.open_mission.spring_janggi.domain.core.Team;

public record GameStatusResponse(
        int moveCount,
        Team currentTurn,
        GameStatus status
) {}
