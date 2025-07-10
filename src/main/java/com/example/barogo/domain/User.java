package com.example.barogo.domain;

import com.example.barogo.type.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "barogo_user")
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "pw", nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(name = "pw_exp_date", nullable = false)
    private LocalDate passwordExpireDate;

    @Column(name = "use_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private char useYn;

    @Column(name="start_use_dttm", nullable = false)
    private LocalDateTime startUseDttm;

    @Column(name="finish_use_dttm")
    private LocalDateTime finishUseDttm;

    @Column(name="refresh_token")
    private String refreshToken;

    @Column(name="fail_cnt")
    private int failedCount;      // 크리덴셜 스터핑 대응

    @Column(name = "cre_dttm", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "cre_user_id", nullable = false)
    private String createUser;

    @Column(name = "upd_dttm")
    private LocalDateTime updateDate;

    @Column(name = "upd_user_id")
    private String updateUser;

    public User(){}

    public boolean isPasswordExpired() {
        return LocalDate.now().isAfter(this.passwordExpireDate);
    }

    public boolean isActive() {
        return this.useYn == 'Y';
    }
}
