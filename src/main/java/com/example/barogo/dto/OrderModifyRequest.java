package com.example.barogo.dto;

import com.example.barogo.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderModifyRequest {
    private String zipCode;
    private String city;
    private String district;
    private String detail;
    private String phone;
    private String recipientName;
    private String addressName;
    private String memo;
}
