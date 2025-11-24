package com.woowa.open_mission.spring_janggi.domain.repository;

import com.woowa.open_mission.spring_janggi.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE g.status = 'FINISHED' AND (g.choPlayer.id = :memberId OR g.hanPlayer.id = :memberId) ORDER BY g.finishedAt DESC")
    List<Game> findFinishedGamesByMemberId(@Param("memberId") Long memberId);
}
