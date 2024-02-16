package com.encore.ordering.order.dto;

import com.encore.ordering.order.domain.Ordering;
import com.encore.ordering.order.domain.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderResDto {
    private Long id;
    private String email;
    private String orderStatus;
    private LocalDateTime createdTime;
    private List<OrderResItemDto> orderItems;

    @Data
    public static class OrderResItemDto {
        private Long id;
        private String itemName;
        private int count;
    }

    public static OrderResDto toDto(Ordering ordering) {
        OrderResDto orderResDto = new OrderResDto();
        orderResDto.setId(ordering.getId());
//        orderResDto.setEmail(ordering.getMember().getEmail());
        orderResDto.setOrderStatus(ordering.getOrderStatus().toString());
        orderResDto.setCreatedTime(ordering.getCreatedTime());
        List<OrderResItemDto> orderResItemDtos = new ArrayList<>();
        for (OrderItem orderItem : ordering.getOrderItems()) {
            OrderResDto.OrderResItemDto dto = new OrderResItemDto();
            dto.setId(orderItem.getId());
//            dto.setItemName(orderItem.getItem().getName());
            dto.setCount(orderItem.getQuantity());
            orderResItemDtos.add(dto);
        }
        orderResDto.setOrderItems(orderResItemDtos);
        return orderResDto;
    }
}
