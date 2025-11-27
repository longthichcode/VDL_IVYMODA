package com.example.ivymoda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// PayOSRequest.java
public class PayOSRequest {
    public long orderCode;
    public int amount;
    public String description;
    public List<Item> items;
    public String returnUrl = "ivymoda://payment-success";
    public String cancelUrl = "ivymoda://payment-cancel";
    public String signature;

    public static class Item {
        public String name;
        public int quantity;
        public int price;
        public Item(String name, int quantity, int price) {
            this.name = name; this.quantity = quantity; this.price = price;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("amount", amount);
        map.put("cancelUrl", cancelUrl);
        map.put("description", description);
        map.put("orderCode", orderCode);
        map.put("returnUrl", returnUrl);

        // === CHUYỂN ITEMS ĐÚNG FORMAT PAYOS YÊU CẦU ===
        List<Map<String, Object>> itemsList = new ArrayList<>();
        for (Item item : items) {
            Map<String, Object> itemMap = new LinkedHashMap<>();
            itemMap.put("name", item.name);
            itemMap.put("quantity", item.quantity);
            itemMap.put("price", item.price);
            itemsList.add(itemMap);
        }
        map.put("items", itemsList);

        return map;
    }
}