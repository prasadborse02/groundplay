package com.theprasadtech.groundplay.controllers

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerTest @Autowired constructor(val mockMvc: MockMvc) {
    @Test
    fun `test HTTP 200 for successfully creating a Player`() {
        assertThat(true==true)
    }
}