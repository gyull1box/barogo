package com.example.barogo.type;

import lombok.Getter;

@Getter
public enum OrderStatusType {
    NEW("1000"),
    PLANNED("2000"),
    CONFIRM("3000"),
    READY_TO_DELIVERY("4000"),
    DELIVERY("5000"),
    DELIVERED("6000"),
    CANCELLED("9999");

    private final String status;

    OrderStatusType(String status) {
        this.status = status;
    }


    public static OrderStatusType fromCode(String status) {
        for (OrderStatusType type : OrderStatusType.values()) {
            if (type.status.equals(status)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown : " + status);
    }

}
