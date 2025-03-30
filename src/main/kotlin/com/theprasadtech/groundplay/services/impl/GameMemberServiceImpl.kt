package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity
import com.theprasadtech.groundplay.repositories.GameMemberRepository
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.GameMemberService
import org.springframework.stereotype.Service

@Service
class GameMemberServiceImpl(
    private val gameRepository: GameRepository,
    private val gameMemberRepository: GameMemberRepository,
    private val playerRepository: PlayerRepository,
) : GameMemberService {
    override fun create(gameMemberEntity: GameMemberEntity): GameMemberEntity {
        require(null == gameMemberEntity.id)
        check(gameRepository.existsById(gameMemberEntity.gameId)) { "Game does not exist !!!" }
        check(playerRepository.existsById(gameMemberEntity.playerId)) { "Player does not exists !!!" }
        check(!gameMemberRepository.existsByGameIdAndPlayerId(gameMemberEntity.gameId, gameMemberEntity.playerId)) {
            "Player Already Registered for the game !!!"
        }
        // TODO: Add validation same player can't register for multiple games at same time
        return gameMemberRepository.save(gameMemberEntity)
    }
}
