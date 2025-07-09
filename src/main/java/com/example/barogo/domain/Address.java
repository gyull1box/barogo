package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addr_id")
    private Long addrId;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lon;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String detail;

    private String phone;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "cre_dttm",nullable = false)
    private Date createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private Date updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public Address(){}
}
