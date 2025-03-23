package com.theprasadtech.groundplay

import com.theprasadtech.groundplay.domain.dto.PlayerDto
import com.theprasadtech.groundplay.domain.entities.PlayerEntity

fun PlayerEntity.toPlayerDto() = PlayerDto(
    id=this.id,
    name=this.name,
    phone=this.phone
)

fun PlayerDto.toPlayerEntity() = PlayerEntity(
    id=this.id,
    name=this.name,
    phone=this.phone
)