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

                        if (Math.abs(dx) == 1 && Math.abs(dy) == 1) {
                            if (!isPalaceDiagonalMove(from, new Position(nx, ny))) {
                                continue;
                            }
                        }

                        targets.add(new Position(nx, ny));
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

        if (!isLegalMove(piece, from, to)) {
            throw new BusinessException(ErrorCode.INVALID_MOVE_RULE);
        }

        pieceMap.remove(from);
        pieceMap.put(to, piece);
    }

    private boolean isLegalMove(Piece piece, Position from, Position to) {
        switch (piece.getType()) {
            case CHA:
                return validateCha(from, to);
            case SOLDIER:
                return validateSoldier(piece.getTeam(), from, to);
            case PO:
                return validatePo(from, to);
            case MA:
                return validateMa(from, to);
            case SANG:
                return validateSang(from, to);
            case GUNG:
            case SA:
                return validateGungSa(from, to);
            default:
                return false;
        }
    }

    // --- 기물별 세부 로직 구현 (예시) ---

    // [차] 직선 이동, 경로에 장애물 없어야 함
    private boolean validateCha(Position from, Position to) {
        // 1. 이동 패턴 확인: 직선인가? (가로 or 세로)
        boolean isStraight = (from.getX() == to.getX()) || (from.getY() == to.getY());

        // 2. 이동 패턴 확인: 궁성 내 대각선인가?
        boolean isPalaceDiagonal = isPalaceDiagonalMove(from, to);

        // 3. 직선도 아니고, 궁성 대각선도 아니면 이동 규칙 위반
        if (!isStraight && !isPalaceDiagonal) {
            return false;
        }

        // 4. 경로상의 장애물 검사 (직선, 대각선 공통)
        // isPathClear는 두 좌표 사이의 경로를 한 칸씩 탐색하므로 대각선 경로 체크도 가능합니다.
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

    private boolean validateSoldier(Team team, Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 1. 기본 이동 거리 검사: 직선 1칸(합 1) 또는 대각선 1칸(합 2)만 허용
        // (2칸 이상 점프하거나, 제자리인 경우는 여기서 걸러짐)
        if (!((absDx == 1 && absDy == 0) || (absDx == 0 && absDy == 1) || (absDx == 1 && absDy == 1))) {
            return false;
        }

        // 2. 방향 정의 (Y좌표 기준 전진 방향)
        // Han(Red, 위쪽): Y가 증가해야 전진 (+1)
        // Cho(Green, 아래쪽): Y가 감소해야 전진 (-1)
        int forwardDy = (team == Team.HAN) ? 1 : -1;

        // 3. [핵심] 후퇴 불가 검사
        // dy가 전진방향의 반대(-forwardDy)라면 무조건 실패
        // 예: Han이 -1(위)로 가려하거나, Cho가 +1(아래)로 가려할 때
        if (dy == -forwardDy) {
            return false;
        }

        // 4. 이동 타입별 상세 검증

        // 4-1. 직선 이동 (좌우 또는 전진)
        if (absDx + absDy == 1) {
            // 좌우(dy=0)이거나 전진(dy=forwardDy)이면 OK (후퇴는 위에서 걸러짐)
            return true;
        }

        // 4-2. 대각선 이동 (absDx=1, absDy=1)
        if (absDx == 1 && absDy == 1) {
            // 졸은 대각선으로 '옆'이나 '뒤'로 못 감. 오직 '전진 대각선'만 가능
            if (dy != forwardDy) {
                return false;
            }
            // 궁성 라인 위인지 확인
            return isPalaceDiagonalMove(from, to);
        }

        return false;
    }

    // [마]: 날일(日)자 이동 + 멱 체크
    private boolean validateMa(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 1. 세로로 긴 날일자 (|dx|=1, |dy|=2)
        // 예: 위로 두 칸, 옆으로 한 칸
        if (absDx == 1 && absDy == 2) {
            // 멱 위치 계산: 출발지에서 Y방향으로 1칸 떨어진 곳
            // (dy가 2면 +1, dy가 -2면 -1 위치가 멱)
            Position myeok = new Position(from.getX(), from.getY() + (dy / 2));

            // 멱에 기물이 있으면 이동 불가
            if (pieceMap.containsKey(myeok)) {
                return false;
            }
            return true;
        }

        // 2. 가로로 긴 날일자 (|dx|=2, |dy|=1)
        // 예: 오른쪽으로 두 칸, 위로 한 칸
        if (absDx == 2 && absDy == 1) {
            // 멱 위치 계산: 출발지에서 X방향으로 1칸 떨어진 곳
            // (dx가 2면 +1, dx가 -2면 -1 위치가 멱)
            Position myeok = new Position(from.getX() + (dx / 2), from.getY());

            // 멱에 기물이 있으면 이동 불가
            if (pieceMap.containsKey(myeok)) {
                return false;
            }
            return true;
        }

        // 3. 마의 이동 패턴이 아님
        return false;
    }

    // [상]: 쓸용(用)자 이동 + 멱 체크
    private boolean validateSang(Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 1. 세로로 긴 이동 (|dx|=2, |dy|=3)
        // 예: 위로 3칸, 옆으로 2칸
        if (absDx == 2 && absDy == 3) {
            // 멱1: 출발지에서 Y방향으로 1칸 (직선 경로)
            Position myeok1 = new Position(from.getX(), from.getY() + (dy / 3));

            // 멱2: 출발지에서 Y방향으로 2칸, X방향으로 1칸 (마의 도착지점과 동일)
            Position myeok2 = new Position(from.getX() + (dx / 2), from.getY() + (dy / 3) * 2);

            // 두 멱 중 하나라도 막혀있으면 이동 불가
            if (pieceMap.containsKey(myeok1) || pieceMap.containsKey(myeok2)) {
                return false;
            }
            return true;
        }

        // 2. 가로로 긴 이동 (|dx|=3, |dy|=2)
        // 예: 오른쪽으로 3칸, 위로 2칸
        if (absDx == 3 && absDy == 2) {
            // 멱1: 출발지에서 X방향으로 1칸 (직선 경로)
            Position myeok1 = new Position(from.getX() + (dx / 3), from.getY());

            // 멱2: 출발지에서 X방향으로 2칸, Y방향으로 1칸 (마의 도착지점과 동일)
            Position myeok2 = new Position(from.getX() + (dx / 3) * 2, from.getY() + (dy / 2));

            // 두 멱 중 하나라도 막혀있으면 이동 불가
            if (pieceMap.containsKey(myeok1) || pieceMap.containsKey(myeok2)) {
                return false;
            }
            return true;
        }

        // 3. 상의 이동 패턴이 아님
        return false;
    }

    // [포]: 직선 이동 + 다리 1개 + 포끼리 못 넘음/못 잡음
    private boolean validatePo(Position from, Position to) {
        // 1. 이동 패턴 확인: 직선인가?
        boolean isStraight = (from.getX() == to.getX()) || (from.getY() == to.getY());

        // 2. 이동 패턴 확인: 궁성 내 대각선인가?
        boolean isPalaceDiagonal = isPalaceDiagonalMove(from, to);

        // 3. 직선도 아니고 대각선도 아니면 이동 불가
        if (!isStraight && !isPalaceDiagonal) {
            return false;
        }

        // 4. [규칙] 도착지에 있는 기물이 '포'인지 확인 (포는 포를 잡을 수 없음)
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

        // 7. [규칙] 그 다리가 '포'인지 확인 (포는 포를 넘을 수 없음)
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

    private boolean validateGungSa(Position from, Position to) {
        // 1. [규칙] 도착지가 궁성(Palace) 범위를 벗어나면 안 됨
        // (출발지는 이미 궁/사가 있는 곳이니 당연히 궁성 안임)
        if (!isInPalace(to.getX(), to.getY())) {
            return false;
        }

        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);

        // 2. [규칙] 이동 거리는 반드시 1칸이어야 함 (직선 1칸 or 대각선 1칸)
        // dx, dy가 0, 1 범위를 넘어가거나 제자리(0,0)면 불가
        if (absDx > 1 || absDy > 1 || (absDx == 0 && absDy == 0)) {
            return false;
        }

        // 3. 이동 패턴별 검증

        // 3-1. 직선 이동 (가로 1칸 or 세로 1칸)
        if (absDx + absDy == 1) {
            // 궁성 안에서는 모든 칸이 직선으로 연결되어 있으므로 무조건 통과
            return true;
        }

        // 3-2. 대각선 이동 (가로 1칸 + 세로 1칸)
        if (absDx == 1 && absDy == 1) {
            // 대각선 이동은 'X'자 길 위에서만 가능
            return isPalaceDiagonalMove(from, to);
        }

        return false;
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
                return false; // 가는 길에 뭔가 있다 -> 이동 불가
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    // === Getters ===
    public Map<Position, Piece> getPieceMap() {
        return new HashMap<>(pieceMap); // 외부 변조 방지를 위해 복사본 반환
    }

    public Piece getPiece(Position position) {
        return pieceMap.get(position);
    }

    public Piece getPiece(int x, int y) {
        return pieceMap.get(new Position(x, y));
    }
}