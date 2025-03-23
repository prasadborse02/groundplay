package com.theprasadtech.groundplay.controllers

import com.theprasadtech.groundplay.domain.dto.PlayerDto
import com.theprasadtech.groundplay.services.PlayerService
import com.theprasadtech.groundplay.toPlayerDto
import com.theprasadtech.groundplay.toPlayerEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PlayerController(private val playerService: PlayerService) {

    @PostMapping(path = ["/v1/player"])
    fun createPlayer(@RequestBody playerDto: PlayerDto): PlayerDto {
        return playerService.save(playerDto.toPlayerEntity()).toPlayerDto()
    }

}