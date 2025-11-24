package com.woowa.open_mission.spring_janggi.controller.dto;

import com.woowa.open_mission.spring_janggi.domain.entity.MoveHistory;

public record MoveHistoryResponse(
        int moveNumber,
        String movedPiece,
        String from,
        String to,
        String capturedPiece
) {
    public static MoveHistoryResponse from(MoveHistory entity) {
        return new MoveHistoryResponse(
                entity.getMoveNumber(),
                entity.getMovedPiece(),
                entity.getFromPosition(),
                entity.getToPosition(),
                entity.getCapturedPiece()
        );
    }
}
