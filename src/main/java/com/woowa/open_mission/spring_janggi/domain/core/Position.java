package com.woowa.open_mission.spring_janggi.domain.core;

import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;

import java.util.Objects;

public final class Position {

    private static final int MAX_X = 8;
    private static final int MAX_Y = 9;

    private final int x;
    private final int y;

    public Position(int x, int y) {
        validate(x, y);
        this.x = x;
        this.y = y;
    }

    public Position(String code) {
        if (code == null || code.length() < 2 || code.length() > 3) {
            throw new BusinessException(ErrorCode.INVALID_POSITION_FORMAT);
        }

        char colChar = Character.toUpperCase(code.charAt(0));
        int parsedX = colChar - 'A';

        int parsedY;
        try {
            parsedY = Integer.parseInt(code.substring(1)) - 1;
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.INVALID_ROW_FORMAT);
        }

        validate(parsedX, parsedY);

        this.x = parsedX;
        this.y = parsedY;
    }

    private void validate(int x, int y) {
        if (x < 0 || x > MAX_X) {
            throw new BusinessException(ErrorCode.POSITION_X_OUT_OF_BOUNDS);
        }
        if (y < 0 || y > MAX_Y) {
            throw new BusinessException(ErrorCode.POSITION_Y_OUT_OF_BOUNDS);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public Position move(int dx, int dy) {
        int newX = this.x + dx;
        int newY = this.y + dy;

        if (isValid(newX, newY)) {
            return new Position(newX, newY);
        }
        return null;
    }

    public static boolean isValid(int x, int y) {
        return x >= 0 && x <= MAX_X && y >= 0 && y <= MAX_Y;
    }


    @Override
    public String toString() {
        char colChar = (char) ('A' + x);
        int rowNum = y + 1;
        return String.valueOf(colChar) + rowNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
