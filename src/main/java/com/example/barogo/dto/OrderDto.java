package com.example.barogo.dto;

import com.example.barogo.domain.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@AllArgsConstructor
public class OrderDto {

    private String userId;
    private Long orderId;
    private String orderNumber;
    private Date deliveryDate;

    public static OrderDto fromEntity(OrderEntity order) {
        return new OrderDto(
                order.getUserId().getUserId(),
                order.getOrderId(),
                order.getOrderNo(),
                order.getDeliveryDate()
        );
    }
}
