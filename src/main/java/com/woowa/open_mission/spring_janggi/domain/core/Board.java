package com.woowa.open_mission.spring_janggi.domain.core;

import com.woowa.open_mission.spring_janggi.global.exception.BusinessException;
import com.woowa.open_mission.spring_janggi.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private final Map<Position, Piece> pieceMap;

    public Board() {
        this.pieceMap = new HashMap<>();
        initialize();
    }

    public Board(Map<Position, Piece> pieceMap) {
        this.pieceMap = pieceMap;
    }

    private void initialize() {
        // TODO: 장기판을 초기화 할 때, 초나라 한나라가 원하는 포진을 선택할 수 있도록 수정 (마상상마, 마상마상, 상마상마, 상마마상)

        placeBasePieces(Team.HAN, 0, 2, 3);
        placeSpecificPieces(Team.HAN, 0);

        placeBasePieces(Team.CHO, 9, 7, 6);
        placeSpecificPieces(Team.CHO, 9);
    }

    private void placeBasePieces(Team team, int backRow, int poRow, int soldierRow) {
        pieceMap.put(new Position(0, backRow), Piece.of(team, PieceType.CHA));
        pieceMap.put(new Position(8, backRow), Piece.of(team, PieceType.CHA));

        pieceMap.put(new Position(1, poRow), Piece.of(team, PieceType.PO));
        pieceMap.put(new Position(7, poRow), Piece.of(team, PieceType.PO));

        for (int col = 0; col <= 8; col += 2) {
            pieceMap.put(new Position(col, soldierRow), Piece.of(team, PieceType.SOLDIER));
        }
    }

    private void placeSpecificPieces(Team team, int backRow) {
        pieceMap.put(new Position(4, backRow + (team == Team.HAN ? 1 : -1)), Piece.of(team, PieceType.GUNG));

        pieceMap.put(new Position(3, backRow), Piece.of(team, PieceType.SA));
        pieceMap.put(new Position(5, backRow), Piece.of(team, PieceType.SA));

        // TODO: 초나라 한나라가 원하는 포진을 선택할 수 있도록 수정 (마상상마, 마상마상, 상마상마, 상마마상)
        pieceMap.put(new Position(1, backRow), Piece.of(team, PieceType.SANG));
        pieceMap.put(new Position(2, backRow), Piece.of(team, PieceType.MA));
        pieceMap.put(new Position(6, backRow), Piece.of(team, PieceType.SANG));
        pieceMap.put(new Position(7, backRow), Piece.of(team, PieceType.MA));
    }

    public List<Position> getMovablePositions(Position from) {
        Piece piece = pieceMap.get(from);
        List<Position> movables = new ArrayList<>();

        if (piece == null) {
            return movables;
        }

        List<Position> candidates = getTheoreticalMoves(from, piece);

        for (Position to : candidates) {
            if (isLegalMove(piece, from, to)) {
                movables.add(to);
            }
        }

        return movables;
    }

    private List<Position> getTheoreticalMoves(Position from, Piece piece) {
        List<Position> targets = new ArrayList<>();
        int x = from.getX();
        int y = from.getY();
        Team team = piece.getTeam();

        switch (piece.getType()) {
            case MA:
                int[][] maOffsets = {
                        {-1, -2}, {1, -2}, {-2, -1}, {2, -1},
                        {-2, 1}, {2, 1}, {-1, 2}, {1, 2}
                };
                addOffsets(targets, x, y, maOffsets);
                break;

            case SANG:
                int[][] sangOffsets = {
                        {-2, -3}, {2, -3}, {-3, -2}, {3, -2},
                        {-3, 2}, {3, 2}, {-2, 3}, {2, 3}
                };
                addOffsets(targets, x, y, sangOffsets);
                break;

            case SA:
            case GUNG:
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;

                        int nx = x + dx;
                        int ny = y + dy;

                        if (!Position.isValid(nx, ny)) continue;
                        if (!isInPalace(nx, ny)) continue;

                        Position pos = new Position(nx, ny);
                        if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
                            if (!isPalaceDiagonalMove(from, pos)) {
                                continue;
                            }
                        }

                        targets.add(pos);
                    }
                }
                break;

            case SOLDIER:
                addIfValid(targets, x - 1, y);
                addIfValid(targets, x + 1, y);

                int forward = (team == Team.HAN) ? 1 : -1;
                addIfValid(targets, x, y + forward);

                if (isOnPalaceLine(from)) {
                    addIfValid(targets, x - 1, y + forward);
                    addIfValid(targets, x + 1, y + forward);
                }
                break;

            case CHA:
            case PO:
                for (int i = 0; i <= 8; i++) if (i != x) targets.add(new Position(i, y));
                for (int j = 0; j <= 9; j++) if (j != y) targets.add(new Position(x, j));

                if (isOnPalaceLine(from)) {
                    addPalaceDiagonalTargets(targets, from);
                }
                break;
        }

        return targets;
    }

    private boolean isInPalace(int x, int y) {
        if (x < 3 || x > 5) return false;

        boolean isTopPalace = (y >= 0 && y <= 2);
        boolean isBottomPalace = (y >= 7 && y <= 9);

        return isTopPalace || isBottomPalace;
    }

    private void addOffsets(List<Position> targets, int cx, int cy, int[][] offsets) {
        for (int[] offset : offsets) {
            addIfValid(targets, cx + offset[0], cy + offset[1]);
        }
    }

    private void addIfValid(List<Position> targets, int x, int y) {
        if (Position.isValid(x, y)) {
            targets.add(new Position(x, y));
        }
    }

    private void addPalaceDiagonalTargets(List<Position> targets, Position from) {
        int x = from.getX();
        int y = from.getY();

        int[][] diagOffsets = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] d : diagOffsets) {
            int dx = d[0];
            int dy = d[1];

            for (int step = 1; step <= 2; step++) {
                int nx = x + (dx * step);
                int ny = y + (dy * step);

                if (Position.isValid(nx, ny) && isOnPalaceLine(new Position(nx, ny))) {
                    targets.add(new Position(nx, ny));
                }
            }
        }
    }

    public void move(Team currentTurn, Position from, Position to) {
        Piece piece = pieceMap.get(from);

        if (piece == null) {
            throw new BusinessException(ErrorCode.PIECE_NOT_FOUND);
        }

        if (piece.getTeam() != currentTurn) {
            throw new BusinessException(ErrorCode.IS_NOT_YOUR_TURN);
        }

        if (from.equals(to)) {
            throw new BusinessException(ErrorCode.CANNOT_MOVE_TO_SAME_POSITION);
        }

        Piece target = pieceMap.get(to);
        if (piece.isSameTeam(target)) {
            throw new BusinessException(ErrorCode.CANNOT_CAPTURE_SAME_TEAM);
        }

        if (!validatePhysicalMove(piece, from, to)) {
            throw new BusinessException(ErrorCode.INVALID_MOVE_RULE);
        }

        pieceMap.remove(from);
        pieceMap.put(to, piece);

        boolean isSuicideMove = isKingInCheck(currentTurn);

        if (isSuicideMove) { // 자살 수인 경우 원상 복구 후 BusinessException을 발생시킴.
            pieceMap.remove(to);
            pieceMap.put(from, piece);
            if (target != null) {
                pieceMap.put(to, target);
            }
            throw new BusinessException(ErrorCode.KING_IS_IN_DANGER);
        }
    }

    private boolean isLegalMove(Piece piece, Position from, Position to) {
        if (!validatePhysicalMove(piece, from, to)) {
            return false;
        }

        Piece target = pieceMap.get(to);

        // 왕을 직접 공격하는 상황은 발생하지 않도록 조정
        if (target != null && target.getType() == PieceType.GUNG) {
            return false;
        }

        // 가상으로 이동 수행 -> 이동했을 때 상대의 장군 상태가 나오는지 확인
        pieceMap.remove(from);
        pieceMap.put(to, piece);

        boolean isSuicideMove = isKingInCheck(piece.getTeam());

        pieceMap.remove(to);
        pieceMap.put(from, piece);
        if (target != null) {
            pieceMap.put(to, target);
        }

        return !isSuicideMove; // isSuicideMove == true 인 경우 자살수 이므로 이동이 불가능 하다는 false 를 반환하여야 함.
    }

    public boolean isKingInCheck(Team team) {
        Position kingPos = findKingPosition(team);

        for (Map.Entry<Position, Piece> entry : pieceMap.entrySet()) {
            Piece enemy = entry.getValue();
            Position enemyPos = entry.getKey();

            if (enemy.getTeam() == team) continue;

            if (validatePhysicalMove(enemy, enemyPos, kingPos)) {
                return true;
            }
        }
        return false;
    }

    private Position findKingPosition(Team team) {
        for (Map.Entry<Position, Piece> entry : pieceMap.entrySet()) {
            Piece p = entry.getValue();
            if (p.getTeam() == team && p.getType() == PieceType.GUNG) {
                return entry.getKey();
            }
        }
        throw new BusinessException(ErrorCode.GAME_DATA_ERROR);
    }

    private boolean validatePhysicalMove(Piece piece, Position from, Position to) {
        // 1. 장기판 범위 밖인지 확인
        if (!Position.isValid(to.getX(), to.getY())) return false;

        // 2. 제자리인지 확인
        if (from.equals(to)) return false;

        Piece target = pieceMap.get(to);

        if (piece.isSameTeam(target)) {
            return false;
        }

        return switch (piece.getType()) {
            case CHA -> validateCha(from, to);
            case SOLDIER -> validateSoldier(piece.getTeam(), from, to);
            case PO -> validatePo(from, to);
            case MA -> validateMa(from, to);
            case SANG -> validateSang(from, to);
            case GUNG, SA -> validateGungSa(from, to);
            default -> false;
        };
    }

    // 기물 설명 [차] : 직선 이동(궁성 내 대각 이동도 가능), 경로에 장애물 없어야 함
    private boolean validateCha(Position from, Position to) {
        // 직선 경로에 여부 확인 (가로 or 세로)
        boolean isStraight = (from.getX() == to.getX()) || (from.getY() == to.getY());

        // 궁성 내 대각 이동 가능 여부 확인
        boolean isPalaceDiagonal = isPalaceDiagonalMove(from, to);

        // 직선도 아니고, 궁성 대각선도 아니면 이동 규칙 위반
        if (!isStraight && !isPalaceDiagonal) {
            return false;
        }

        // 경로상의 장애물 검사 (직선, 대각선 공통)
        return isPathClear(from, to);
    }

    private boolean isPalaceDiagonalMove(Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());

        if (dx == 0 || dy == 0 || dx != dy) {
            return false;
        }

        if (!isOnPalaceLine(from) || !isOnPalaceLine(to)) {
            return false;
        }
        return dx <= 2;
    }

    private boolean isOnPalaceLine(Position p) {
        int x = p.getX();
        int y = p.getY();

        if (x < 3 || x > 5) return false;

        boolean isTopPalace = (y >= 0 && y <= 2);
        boolean isBottomPalace = (y >= 7 && y <= 9);
        if (!isTopPalace && !isBottomPalace) return false;

        if (x == 4) {
            return (y == 1 || y == 8);
        }

        return (y == 0 || y == 2 || y == 7 || y == 9);
    }

    // 기물 설명 [졸/병] : 직선으로 1칸 이동(궁성 내 대각 이동도 가능), 후퇴가 불가능 함
    private boolean validateSoldier(Team team, Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 1칸 이동 (직선, 대각선)
        if (!((absDx == 1 && absDy == 0) || (absDx == 0 && absDy == 1) || (absDx == 1 && absDy == 1))) {
            return false;
        }

        // 방향 정의 (Y좌표 기준 전진 방향) - Cho는 Y가 감소해야 전진, Han은 Y가 증가해야 전진
        int forwardDy = (team == Team.HAN) ? 1 : -1;

        // 후퇴 불가
        if (dy == -forwardDy) {
            return false;
        }

        // 직선 이동 (좌우 또는 전진)
        if (absDx + absDy == 1) {
            return true;
        }

        // 대각선 이동 (absDx=1, absDy=1)
        if (absDx == 1 && absDy == 1) {
            return isPalaceDiagonalMove(from, to);
        }

        return false;
    }

    // 기물 설명 [마]: 날일(日)자 이동 + 멱 체크
    private boolean validateMa(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 1. 세로로 긴 날일자 (|dx|=1, |dy|=2)
        if (absDx == 1 && absDy == 2) {
            // 멱 위치 계산: 출발지에서 Y방향으로 1칸 떨어진 곳
            Position myeok = new Position(from.getX(), from.getY() + (dy / 2));

            if (pieceMap.containsKey(myeok)) {
                return false;
            }
            return true;
        }

        // 2. 가로로 긴 날일자 (|dx|=2, |dy|=1)
        if (absDx == 2 && absDy == 1) {
            // 멱 위치 계산: 출발지에서 X방향으로 1칸 떨어진 곳
            Position myeok = new Position(from.getX() + (dx / 2), from.getY());

            if (pieceMap.containsKey(myeok)) {
                return false;
            }
            return true;
        }

        // 3. 마의 이동 패턴이 아님
        return false;
    }

    // 기물 설명 [상]: 쓸용(用)자 이동 + 멱 체크
    private boolean validateSang(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 1. 세로로 긴 이동 (|dx|=2, |dy|=3)
        if (absDx == 2 && absDy == 3) {
            Position myeok1 = new Position(from.getX(), from.getY() + (dy / 3));

            Position myeok2 = new Position(from.getX() + (dx / 2), from.getY() + (dy / 3) * 2);

            if (pieceMap.containsKey(myeok1) || pieceMap.containsKey(myeok2)) {
                return false;
            }
            return true;
        }

        // 2. 가로로 긴 이동 (|dx|=3, |dy|=2)
        if (absDx == 3 && absDy == 2) {
            Position myeok1 = new Position(from.getX() + (dx / 3), from.getY());

            Position myeok2 = new Position(from.getX() + (dx / 3) * 2, from.getY() + (dy / 2));

            if (pieceMap.containsKey(myeok1) || pieceMap.containsKey(myeok2)) {
                return false;
            }
            return true;
        }

        // 3. 상의 이동 패턴이 아님
        return false;
    }

    // 기물 설명 [포]: 직선 이동 + 다리 1개 + 포끼리 못 넘음/못 잡음
    private boolean validatePo(Position from, Position to) {
        // 직선 경로인지 확인
        boolean isStraight = (from.getX() == to.getX()) || (from.getY() == to.getY());

        // 궁성 내 대각선인지 확인
        boolean isPalaceDiagonal = isPalaceDiagonalMove(from, to);

        // 직선도 아니고 대각선도 아니면 이동 불가
        if (!isStraight && !isPalaceDiagonal) {
            return false;
        }

        // [규칙 : 포는 포를 잡을 수 없음] 도착지에 있는 기물이 '포'인지 확인
        Piece target = pieceMap.get(to);
        if (target != null && target.getType() == PieceType.PO) {
            return false;
        }

        // 5. 경로상에 있는 기물(다리)들을 모두 가져옴
        List<Piece> bridges = getPiecesOnPath(from, to);

        // 6. [규칙] 다리는 정확히 1개여야 함
        if (bridges.size() != 1) {
            return false; // 다리가 없거나, 2개 이상이면 이동 불가
        }

        // [규칙 : 포는 포를 넘을 수 없음] 다리가 '포'인지 확인
        Piece bridge = bridges.get(0);
        if (bridge.getType() == PieceType.PO) {
            return false;
        }

        return true;
    }

    private List<Piece> getPiecesOnPath(Position from, Position to) {
        List<Piece> pieces = new ArrayList<>();

        int x = from.getX();
        int y = from.getY();
        int destX = to.getX();
        int destY = to.getY();

        // 이동 방향 단위 벡터 (-1, 0, 1) 구하기
        int dx = Integer.compare(destX, x);
        int dy = Integer.compare(destY, y);

        // 한 칸 전진
        x += dx;
        y += dy;

        // 목적지에 도달하기 전까지 반복
        while (x != destX || y != destY) {
            Position pos = new Position(x, y);
            if (pieceMap.containsKey(pos)) {
                pieces.add(pieceMap.get(pos));
            }
            x += dx;
            y += dy;
        }

        return pieces;
    }

    // 기물 설명 [궁사] : 궁성 범위 안에서 이동, 한 칸씩 이동 (직선, 대각선 이동 가능)
    private boolean validateGungSa(Position from, Position to) {
        if (!isInPalace(to.getX(), to.getY())) {
            return false;
        }

        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        if (absDx > 1 || absDy > 1 || (absDx == 0 && absDy == 0)) {
            return false;
        }

        // 직선 이동 (가로 1칸 or 세로 1칸)
        if (absDx + absDy == 1) {
            return true;
        }

        // 대각선 이동 (가로 1칸 + 세로 1칸)
        if (absDx == 1 && absDy == 1) {
            return isPalaceDiagonalMove(from, to);
        }

        return false;
    }

    public boolean isCheckmate(Team team) {
        // 1. 현재 장군 상태가 아니면 외통수일 수 없음
        if (!isKingInCheck(team)) {
            return false;
        }

        // 2. 내 모든 기물을 하나씩 움직여보며, 장군을 피할 수 있는지 확인
        List<Position> myPieces = new ArrayList<>();
        for (Map.Entry<Position, Piece> entry : pieceMap.entrySet()) {
            if (entry.getValue().getTeam() == team) {
                myPieces.add(entry.getKey());
            }
        }

        for (Position from : myPieces) {
            // 이 기물이 갈 수 있는 모든 곳을 조회 (getMovablePositions가 내부적으로 isLegalMove 호출 -> 자살수 체크됨)
            List<Position> moves = getMovablePositions(from);

            // 갈 곳이 하나라도 있다면(즉, 장군을 피하는 수가 있다면) 외통수가 아님
            if (!moves.isEmpty()) {
                return false;
            }
        }

        // 3. 모든 기물을 다 살펴봤는데도 피할 수가 없음 -> 외통수!
        return true;
    }

    // 직선 경로 장애물 확인 유틸리티
    private boolean isPathClear(Position from, Position to) {
        int x = from.getX();
        int y = from.getY();
        int destX = to.getX();
        int destY = to.getY();

        int dx = Integer.compare(destX, x); // -1, 0, 1
        int dy = Integer.compare(destY, y); // -1, 0, 1

        x += dx;
        y += dy;

        while (x != destX || y != destY) {
            if (pieceMap.containsKey(new Position(x, y))) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    public double calculateScore(Team team) {
        double score = 0.0;

        if (team == Team.HAN) {
            score += 1.5;
        }

        for (Piece piece : pieceMap.values()) {
            if (piece.getTeam() == team) {
                score += piece.getScore();
            }
        }

        return score;
    }

    public Map<Position, Piece> getPieceMap() {
        return new HashMap<>(pieceMap);
    }

    public Piece getPiece(Position position) {
        return pieceMap.get(position);
    }

    public Piece getPiece(int x, int y) {
        return pieceMap.get(new Position(x, y));
    }
}