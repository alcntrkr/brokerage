package com.ing.brokerage.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CreateOrderRequest {
    @NotNull
    public String asset;
    @NotNull
    public String side;
    @NotNull
    public Long size;
    @NotNull
    public BigDecimal price;
    public String customerId; // for admin only
}
