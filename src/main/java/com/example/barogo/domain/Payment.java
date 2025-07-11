package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_seq_gen")
    @SequenceGenerator(name = "payment_seq_gen", sequenceName = "PAYMENT_ID", allocationSize = 1)
    @Column(name = "payment_id")
    private Long paymentMethodId;

    @Column(name = "code", nullable = false)
    private String methodCode;

    @Column(name = "name", nullable = false)
    private String methodName;

    @Column(name = "use_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private char useYn = 'Y';

    @Column(name = "cre_dttm",nullable = false)
    private LocalDateTime createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private LocalDateTime updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;
}
