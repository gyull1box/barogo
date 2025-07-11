package com.example.barogo.domain;

import com.example.barogo.type.OrderStatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "orders", indexes = {@Index(name="idx_order_01", columnList = "user_id, delivery_date")})
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq_gen")
    @SequenceGenerator(name = "order_seq_gen", sequenceName = "ORDER_ID", allocationSize = 1)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User userId;

    @OneToOne
    @JoinColumn(name="frm_addr_id", nullable = false)
    private Address frmAddress;

    @OneToOne
    @JoinColumn(name="to_addr_id", nullable = false)
    private Address toAddress;

    @Column(name = "delivery_date",nullable = false)
    private Date deliveryDate;

    @Column(nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String orderStatus;

    @Column(name = "ttl_volume",nullable = false)
    private Long ttlVolume = 0L;

    @Column(name = "ttl_weight",nullable = false)
    private Long ttlWeight = 0L;

    @Column(name = "ttl_price",nullable = false)
    private Long ttlPrice = 0L;

    @Column(name = "delivery_fee",nullable = false)
    private Long deliveryFee = 0L;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dspch_no")
    private Dispatch dspchNo;

    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Column(name = "del_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char deleteYn = 'N';

    private String memo;

    @Column(name = "cre_dttm",nullable = false)
    private LocalDateTime createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private LocalDateTime updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public OrderEntity(){}
}
