package com.theprasadtech.groundplay.services

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity

interface GameMemberService {
    fun create(gameMemberEntity: GameMemberEntity): GameMemberEntity
}
