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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dspch_seq_gen")
    @SequenceGenerator(name = "dspch_seq_gen", sequenceName = "DSPCH_NO", allocationSize = 1)
    @Column(name = "dspch_no")
    private Long dspchNo;

    @OneToOne
    @JoinColumn(name="drvr_id", nullable = false)
    private Driver driver;

    // 미개발
}
