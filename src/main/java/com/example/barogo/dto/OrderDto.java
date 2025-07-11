package com.example.barogo.dto;

import com.example.barogo.domain.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String userId;
    private Long orderId;
    private String orderNumber;
    private Date deliveryDate;
    private String status;

    public static OrderDto fromEntity(OrderEntity order) {
        return new OrderDto(
                order.getUserId().getUserId(),
                order.getOrderId(),
                order.getOrderNo(),
                order.getDeliveryDate(),
                order.getOrderStatus()
        );
    }
}
