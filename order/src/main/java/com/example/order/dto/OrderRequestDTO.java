package com.example.order.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequestDTO {

    private BigDecimal totalAmount;

    private List<OrderItemDTO> items;

}
