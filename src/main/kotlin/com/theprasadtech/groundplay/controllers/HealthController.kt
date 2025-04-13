package com.theprasadtech.groundplay.controllers

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
class HealthController {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> = ResponseEntity.ok("pong")
}
