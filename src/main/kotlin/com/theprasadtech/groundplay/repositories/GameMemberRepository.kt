package com.theprasadtech.groundplay.repositories

import com.theprasadtech.groundplay.domain.entities.GameMembersEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GameMemberRepository : JpaRepository<GameMembersEntity, Long?>
