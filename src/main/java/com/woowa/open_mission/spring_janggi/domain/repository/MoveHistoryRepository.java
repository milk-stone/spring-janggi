package com.woowa.open_mission.spring_janggi.domain.repository;

import com.woowa.open_mission.spring_janggi.domain.entity.MoveHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveHistoryRepository extends JpaRepository<MoveHistory, Long> {
    List<MoveHistory> findAllByGameIdOrderByMoveNumberAsc(Long gameId);
}
