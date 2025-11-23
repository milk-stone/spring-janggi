package com.woowa.open_mission.spring_janggi.domain.core;

import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;

import java.util.Objects;

/**
 * 장기 알(기물) 하나를 나타내는 불변 객체
 */
public final class Piece {

    private final Team team;
    private final PieceType type;

    public Piece(Team team, PieceType type) {
        if (team == null || type == null) {
            throw new BusinessException(ErrorCode.PIECE_PARAMETER_REQUIRED);
        }
        this.team = team;
        this.type = type;
    }

    public static Piece of(Team team, PieceType type) {
        return new Piece(team, type);
    }

    public Team getTeam() {
        return team;
    }

    public PieceType getType() {
        return type;
    }

    public double getScore() {
        return type.getScore();
    }

    public boolean isEnemy(Piece other) {
        return other != null && this.team != other.team;
    }

    public boolean isSameTeam(Piece other) {
        return other != null && this.team == other.team;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return team == piece.team && type == piece.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, type);
    }

    @Override
    public String toString() {
        return team + "_" + type;
    }
}