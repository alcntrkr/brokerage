package com.ing.brokerage;

import com.ing.brokerage.repository.AssetRepository;
import com.ing.brokerage.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;
    @Autowired AssetRepository assetRepository;
    @Autowired OrderRepository orderRepository;

    @Test
    public void customerCreateBuyOrderFlow() throws Exception {
        var loginReq = mapper.writeValueAsString(java.util.Map.of("id","cust1","password","pass1"));
        var loginRes = mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginReq))
                .andExpect(status().isOk()).andReturn();
        String token = mapper.readTree(loginRes.getResponse().getContentAsString()).get("token").asText();
        // create order
        var orderReq = mapper.writeValueAsString(java.util.Map.of("asset","AAPL","side","BUY","size",1,"price",150.00));
        var res = mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON).header("Authorization","Bearer "+token).content(orderReq))
                .andExpect(status().isOk()).andReturn();
        // verify order created
        var body = res.getResponse().getContentAsString();
        Assertions.assertTrue(body.contains("PENDING"));
    }
}
