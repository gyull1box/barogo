package com.example.barogo.domain;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_detail")
public class OrderDetail {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private OrderEntity orderId;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_dtl_seq_gen")
    @SequenceGenerator(name = "order_dtl_seq_gen", sequenceName = "ORDER_DTL_ID", allocationSize = 1)
    @Column(name="order_dtl_id")
    private Long orderDtlId;

    @Column(nullable = false)
    private Long volumn = 0L;

    @Column(nullable = false)
    private Long weight = 0L;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Long price = 0L;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "cre_dttm",nullable = false)
    private LocalDateTime createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private LocalDateTime updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public OrderDetail(){}
}
