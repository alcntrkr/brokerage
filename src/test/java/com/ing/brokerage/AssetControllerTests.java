package com.ing.brokerage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AssetControllerTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @Test
    public void testLoginAndGetAssets() throws Exception {
        var loginReq = mapper.writeValueAsString(java.util.Map.of("id","cust1","password","pass1"));
        var loginRes = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginReq))
                .andExpect(status().isOk()).andReturn();
        String token = mapper.readTree(loginRes.getResponse().getContentAsString()).get("token").asText();
        mockMvc.perform(get("/api/assets").header("Authorization","Bearer "+token)).andExpect(status().isOk());
    }
}
