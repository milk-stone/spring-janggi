package com.woowa.open_mission.spring_janggi.domain;

import com.woowa.open_mission.spring_janggi.domain.core.Position;
import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositionTest {

    @DisplayName("문자열 좌표(예: A1)를 입력하면 올바른 x, y 인덱스를 가진 Position이 생성된다.")
    @ParameterizedTest
    @CsvSource(value = {"A1,0,0", "I10,8,9", "E5,4,4"})
    void createPositionSuccess(String input, int expectedX, int expectedY) {
        // when
        Position pos = new Position(input);

        // then
        assertThat(pos.getX()).isEqualTo(expectedX);
        assertThat(pos.getY()).isEqualTo(expectedY);
    }

    @DisplayName("잘못된 포맷의 좌표를 입력하면 예외가 발생한다.")
    @Test
    void createPositionFailFormat() {
        assertThatThrownBy(() -> new Position("Z99"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", "PE_003");
    }

    @DisplayName("Position 객체를 문자열로 변환하면 올바른 좌표 표기가 반환된다.")
    @Test
    void toStringTest() {
        Position pos = new Position(0, 0);
        assertThat(pos.toString()).isEqualTo("A1");
    }
}