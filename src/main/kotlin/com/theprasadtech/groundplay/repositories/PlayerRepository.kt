package com.theprasadtech.groundplay.repositories

import com.theprasadtech.groundplay.domain.entities.PlayerEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlayerRepository : JpaRepository<PlayerEntity, Long?> {
    fun findByPhone(phone: String): PlayerEntity?
}
