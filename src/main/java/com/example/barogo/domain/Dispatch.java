package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dspch")
@Getter
@Setter
public class Dispatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dspch_no")
    private Long dspchNo;

    @OneToOne
    @JoinColumn(name="drvr_id", nullable = false)
    private Driver driver;

    // 미개발
}
