package org.talky.platform.app.api.v1.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.talky.platform.support.response.ResultType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("내 정보 조회")
    class GetMe {

        @Test
        @DisplayName("유효한 토큰으로 요청하면 내 정보를 반환한다")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/users/@me")
                            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data.loginId").exists())
                    .andExpect(jsonPath("$.data.nickname").exists())
                    .andExpect(jsonPath("$.data.userTag").exists())
                    .andExpect(jsonPath("$.error").isEmpty());
        }
    }

    @Nested
    @DisplayName("사용자 프로필 조회")
    class GetUserProfile {

        @Test
        @DisplayName("존재하는 사용자를 조회하면 프로필 정보를 반환한다")
        void success() throws Exception {
            mockMvc.perform(get("/api/v1/users/{userTag}", "김철수#5678")
                            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(ResultType.SUCCESS.name()))
                    .andExpect(jsonPath("$.data.nickname").exists())
                    .andExpect(jsonPath("$.data.userTag").exists())
                    .andExpect(jsonPath("$.data.profileMessage").exists())
                    .andExpect(jsonPath("$.data.loginId").doesNotExist())
                    .andExpect(jsonPath("$.error").isEmpty());
        }
    }
}
