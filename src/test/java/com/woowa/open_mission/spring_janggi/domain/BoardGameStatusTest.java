package com.woowa.open_mission.spring_janggi.domain;

import com.woowa.open_mission.spring_janggi.domain.core.*;
import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardGameStatusTest {

    @DisplayName("자살수 방지: 왕이 잡히는 곳으로는 이동할 수 없다.")
    @Test
    void preventSuicideMove() {
        // given
        // 한나라 왕(E1) 앞에 한나라 졸(E2)이 있고, 그 앞에 초나라 차(E9)가 노리고 있음.
        Map<Position, Piece> map = new HashMap<>();
        map.put(new Position("E1"), Piece.of(Team.HAN, PieceType.GUNG));
        map.put(new Position("E2"), Piece.of(Team.HAN, PieceType.SOLDIER));
        map.put(new Position("E9"), Piece.of(Team.CHO, PieceType.CHA));

        Board board = new Board(map);

        // when & then
        // 졸을 옆(F2)으로 치우려고 하면 -> 왕이 죽게 됨 -> 예외 발생해야 함
        assertThatThrownBy(() -> board.move(Team.HAN, new Position("E2"), new Position("F2")))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", "BE_006");
    }

    @DisplayName("외통수: 장군을 피할 수 없으면 외통수(Checkmate)다.")
    @Test
    void checkmateTest() {
        // given
        Map<Position, Piece> map = new HashMap<>();
        map.put(new Position("E1"), Piece.of(Team.HAN, PieceType.GUNG)); // 왕
        map.put(new Position("E2"), Piece.of(Team.CHO, PieceType.CHA));  // 바로 앞 차 (장군)
        map.put(new Position("D1"), Piece.of(Team.CHO, PieceType.CHA));  // 옆 차 (도망 불가)
        map.put(new Position("F1"), Piece.of(Team.CHO, PieceType.CHA));  // 옆 차 (도망 불가)

        Board board = new Board(map);

        // when: 한나라(HAN)가 외통수 상태인지 확인
        boolean isCheckmate = board.isCheckmate(Team.HAN);

        // then
        assertThat(isCheckmate).isTrue();
    }
}