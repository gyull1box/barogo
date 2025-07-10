package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "shpm")
@Getter
@Setter
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shpm_id")
    private Long shpmId;

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
    private String shpmStatus;

    @Column(name = "ttl_volume",nullable = false)
    private Long ttlVolume;

    @Column(name = "ttl_weight",nullable = false)
    private Long ttlWeight;

    @Column(name = "ttl_price",nullable = false)
    private Long ttlPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dspch_no")
    private Dispatch dspchNo;

    @Column(name = "track_no")
    private String trackingNo;

    @Column(name = "cre_dttm",nullable = false)
    private Timestamp createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private Timestamp updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public Shipment(){}
}
