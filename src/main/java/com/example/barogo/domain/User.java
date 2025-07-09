package com.example.barogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "'user'")
public class User {
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "pw", nullable = false)
    private String password;

    private String name;

    @Column(nullable = false)
    private String type;

    @Column(name = "cre_dttm",nullable = false)
    private Date createDate;

    @Column(name = "cre_user_id",nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private Date updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public User(){}
}
