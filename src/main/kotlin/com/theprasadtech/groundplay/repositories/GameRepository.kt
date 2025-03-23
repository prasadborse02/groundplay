package com.theprasadtech.groundplay.repositories

import com.theprasadtech.groundplay.domain.entities.GameEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<GameEntity,Long?>