package com.woowa.open_mission.spring_janggi.domain.repository;

import com.woowa.open_mission.spring_janggi.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
