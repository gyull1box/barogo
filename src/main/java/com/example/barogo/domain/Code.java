package com.example.barogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@Entity
public class Code {
    @Id
    private String code;

    private String name;

    @Column(name = "use_yn", nullable = false)
    private boolean useYn;

    @Column(name = "cre_dttm",nullable = false)
    private Timestamp createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private Timestamp updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

}
