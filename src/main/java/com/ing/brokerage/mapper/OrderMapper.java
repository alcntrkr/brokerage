package com.ing.brokerage.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    // Add mapping methods as needed. Example:
    // OrderDto toDto(Order order);
    // Order toEntity(CreateOrderRequest dto);
}
