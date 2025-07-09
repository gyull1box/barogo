package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class CodeDetail {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    private Code code;

    @Id
    @Column(name = "detail_code")
    private String detaiCode;

    @Column(name = "detail_name", nullable = false)
    private String detailName;

    @Column(name = "lang_tp", nullable = false)
    private String langType;

    @Column(name = "cre_dttm",nullable = false)
    private Date createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private Date updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

}
