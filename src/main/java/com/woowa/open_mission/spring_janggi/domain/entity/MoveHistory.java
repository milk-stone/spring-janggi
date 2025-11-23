package com.woowa.open_mission.spring_janggi.domain.entity;

import jakarta.persistence.*; // Spring Boot 3+
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "move_history") // 테이블명 단수형
public class MoveHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "move_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private int moveNumber;

    @Column(nullable = false)
    private String movedPiece;

    @Column(nullable = false)
    private String fromPosition;

    @Column(nullable = false)
    private String toPosition;

    private String capturedPiece;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public static MoveHistory recordMove(
            Game game,
            int moveNumber,
            String movedPiece,
            String fromPosition,
            String toPosition,
            String capturedPiece
    ) {
        MoveHistory move = new MoveHistory();
        move.game = game;
        move.moveNumber = moveNumber;
        move.movedPiece = movedPiece;
        move.fromPosition = fromPosition;
        move.toPosition = toPosition;
        move.capturedPiece = capturedPiece;
        move.createdAt = LocalDateTime.now();
        return move;
    }
}