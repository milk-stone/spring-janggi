package com.woowa.open_mission.spring_janggi.domain.core;

import lombok.Getter;

@Getter
public enum PieceType {
    GUNG("궁", 0.0),
    SA("사", 3.0),
    CHA("차", 13.0),
    PO("포", 7.0),
    MA("마", 5.0),
    SANG("상", 3.0),
    SOLDIER("졸/병", 2.0);

    private final String name;
    private final double score;

    PieceType(String name, double score) {
        this.name = name;
        this.score = score;
    }
}
