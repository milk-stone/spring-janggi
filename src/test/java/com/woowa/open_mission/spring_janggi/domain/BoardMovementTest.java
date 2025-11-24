package com.woowa.open_mission.spring_janggi.domain;

import com.woowa.open_mission.spring_janggi.domain.core.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class BoardMovementTest {

    private Board createBoardWithPieces(Map<String, Piece> setup) {
        Map<Position, Piece> map = new HashMap<>();
        setup.forEach((posStr, piece) -> map.put(new Position(posStr), piece));
        return new Board(map);
    }

    @DisplayName("마(MA)는 멱이 막히면 이동할 수 없다.")
    @Test
    void maMyeokTest() {
        // given: 마(C5) 앞에 아군 졸(C4)이 있어 멱을 막음
        Map<String, Piece> setup = new HashMap<>();
        setup.put("C5", Piece.of(Team.CHO, PieceType.MA));
        setup.put("C4", Piece.of(Team.CHO, PieceType.SOLDIER)); // 장애물
        // 궁이 없으면
        setup.put("E9", Piece.of(Team.CHO, PieceType.GUNG));
        Board board = createBoardWithPieces(setup);
        Position maPos = new Position("C5");

        // when: 마가 갈 수 있는 곳 조회
        List<Position> movables = board.getMovablePositions(maPos);

        // then: 위쪽 2군데(B3, D3)로는 못 가야 함 (멱이 막혀서)
        assertThat(movables).extracting(Position::toString)
                .doesNotContain("B3", "D3")
                .contains("A6", "E6");
    }

    @DisplayName("포(PO)는 다리가 하나 있어야만 이동할 수 있다.")
    @Test
    void poBridgeTest() {
        // given: 포(A1), 다리(A5), 목적지(A9)
        Map<String, Piece> setup = new HashMap<>();
        setup.put("A1", Piece.of(Team.CHO, PieceType.PO));
        setup.put("A5", Piece.of(Team.HAN, PieceType.CHA));

        setup.put("E9", Piece.of(Team.CHO, PieceType.GUNG));
        Board board = createBoardWithPieces(setup);

        // when
        List<Position> movables = board.getMovablePositions(new Position("A1"));

        // then
        assertThat(movables).extracting(Position::toString)
                .contains("A6", "A9") // 다리 너머로는 갈 수 있음
                .doesNotContain("A2", "A3", "A4"); // 다리 전으로는 못 감
    }

    @DisplayName("포(PO)는 포를 넘을 수 없다.")
    @Test
    void poCannotJumpPo() {
        // given: 포(A1), 또 다른 포(A5 - 다리)
        Map<String, Piece> setup = new HashMap<>();
        setup.put("A1", Piece.of(Team.CHO, PieceType.PO));
        setup.put("A5", Piece.of(Team.HAN, PieceType.PO));

        Board board = createBoardWithPieces(setup);

        // when
        List<Position> movables = board.getMovablePositions(new Position("A1"));

        // then
        assertThat(movables).isEmpty();
    }
}