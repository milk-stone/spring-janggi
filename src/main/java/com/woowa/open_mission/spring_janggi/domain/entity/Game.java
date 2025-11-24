package com.woowa.open_mission.spring_janggi.domain.entity;

import com.woowa.open_mission.spring_janggi.domain.core.GameResult;
import com.woowa.open_mission.spring_janggi.domain.core.GameStatus;
import com.woowa.open_mission.spring_janggi.domain.core.Team;

import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cho_player_id")
    private Member choPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "han_player_id")
    private Member hanPlayer;

    // 핵심: 현재 장기판의 상태를 JSON/TEXT로 저장
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String boardState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Team currentTurn;

    @Column(nullable = false)
    private int moveCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status;

    @Enumerated(EnumType.STRING)
    private GameResult winner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime finishedAt;

    public static Game createGame(String title, Member host, String initialBoardState) {
        Game game = new Game();
        game.title = title;
        game.choPlayer = host;
        game.hanPlayer = null;
        game.boardState = initialBoardState;
        game.status = GameStatus.WAITING;
        game.currentTurn = Team.CHO;
        game.moveCount = 0;
        game.createdAt = LocalDateTime.now();
        return game;
    }

    public void join(Member guest) {
        if (this.status != GameStatus.WAITING) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_NOT_WAITING);
        }
        this.hanPlayer = guest;
        this.status = GameStatus.IN_PROGRESS;
    }

    public void updateMove(String newBoardState, Team nextTurn) {
        if (this.status != GameStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.INVALID_GAME_STATUS);
        }
        this.boardState = newBoardState;
        this.currentTurn = nextTurn;
        this.moveCount++;
    }

    public void finishGame(GameResult winner) {
        if (this.status == GameStatus.FINISHED) {
            return;
        }
        this.status = GameStatus.FINISHED;
        this.winner = winner;
        this.finishedAt = LocalDateTime.now();
    }
}