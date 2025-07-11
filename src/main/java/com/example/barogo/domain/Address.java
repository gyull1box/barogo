package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
    @SequenceGenerator(name = "order_seq_gen", sequenceName = "ORDER_ID", allocationSize = 1)
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

    @Column(name = "name")
    private String addressName; // 회사, 집 ..

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User userId; // 사용자 지정 주소인 경우 사용 (회사, 집)

    @Column(name = "cre_dttm",nullable = false)
    private LocalDateTime createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private LocalDateTime updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public Address(){}
}
