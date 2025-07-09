package com.example.barogo.domain;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "shpm_dtl")
public class ShipmentDetail {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="shpm_id", nullable = false)
    private Shipment shpmId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="shpm_dtl_id")
    private Long shpmDtlId;

    @Column(nullable = false)
    private Long volumn;

    @Column(nullable = false)
    private Long weight;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Long price;

    @Column(name = "cre_dttm",nullable = false)
    private Timestamp createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private Timestamp updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public ShipmentDetail(){}
}
