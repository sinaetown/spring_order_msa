package com.encore.ordering.order.domain;

import lombok.Data;

@Data
public class ItemUpdateQuantityDto {
    private Long id;
    private int stockQuantity;
}
