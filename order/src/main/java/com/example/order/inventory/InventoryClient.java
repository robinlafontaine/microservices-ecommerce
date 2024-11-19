package com.example.order.inventory;

import com.example.order.orderitem.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient {

    @PostMapping("/inventory/products/check")
    boolean checkStock(@RequestBody List<OrderItem> items);

    @PostMapping("/inventory/products/reserve")
    boolean reserveStock(@RequestBody List<OrderItem> items);

    @PostMapping("/inventory/products/free")
    boolean freeStock(@RequestBody List<OrderItem> items);
}

