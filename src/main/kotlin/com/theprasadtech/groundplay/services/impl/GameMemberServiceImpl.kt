package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.domain.entities.GameMemberEntity
import com.theprasadtech.groundplay.repositories.GameMemberRepository
import com.theprasadtech.groundplay.repositories.GameRepository
import com.theprasadtech.groundplay.repositories.PlayerRepository
import com.theprasadtech.groundplay.services.GameMemberService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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
        val gameDetails = gameRepository.findGameById(gameMemberEntity.gameId)
        check(gameDetails.startTime.isAfter(LocalDateTime.now()))
        check(gameDetails.enrolledPlayers < gameDetails.teamSize) { "Game is already full !" }
        if (gameMemberRepository.existsByGameIdAndPlayerId(gameMemberEntity.gameId, gameMemberEntity.playerId)) {
            val existingEntity = gameMemberRepository.findByGameIdAndPlayerId(gameMemberEntity.gameId, gameMemberEntity.playerId)
            val updatedEntity =
                existingEntity.copy(
                    status = gameMemberEntity.status,
                )
            return gameMemberRepository.save(updatedEntity)
        } else {
            check(gameMemberRepository.isPlayerAvailable(gameDetails.startTime, gameDetails.endTime, gameMemberEntity.playerId)) {
                "A player must be available within the duration !!"
            }
            return gameMemberRepository.save(gameMemberEntity)
        }
    }
}
