package com.ashi.sslserverPinning.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PinningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnServerPin() throws Exception {
        mockMvc.perform(get("/api/pinning/server-pin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pin").exists())
                .andExpect(jsonPath("$.sha256Hex").exists());
    }

    @Test
    void shouldFailWhenPinDoesNotMatch() throws Exception {
        mockMvc.perform(post("/api/pinning/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pin\":\"sha256/not-a-real-pin\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matched").value(false));
    }
}

