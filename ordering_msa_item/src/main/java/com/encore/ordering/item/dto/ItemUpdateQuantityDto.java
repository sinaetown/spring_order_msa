package com.encore.ordering.item.dto;

import lombok.Data;

@Data
public class ItemUpdateQuantityDto {
    private Long id;
    private int stockQuantity;
}
