package com.learnandphish.order.inventory;

import com.learnandphish.order.orderitem.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "inventory-service", url = "${inventory.service.url}")
public interface InventoryClient {

    @PostMapping("/api/inventory/check")
    boolean checkStock(@RequestBody List<OrderItem> items);

    @PostMapping("/api/inventory/reserve")
    void reserveStock(@RequestBody List<OrderItem> items);
}

