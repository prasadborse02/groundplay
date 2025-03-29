package com.theprasadtech.groundplay.services.impl

import com.theprasadtech.groundplay.repositories.GameMemberRepository
import com.theprasadtech.groundplay.services.GameMemberService
import org.springframework.stereotype.Service

@Service
class GameMemberServiceImpl(
    private val gameMemberRepository: GameMemberRepository,
) : GameMemberService
