package com.example.inventory.orderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemDTO {

    private Long id;
    private Long productId;
    private int quantity;

}
