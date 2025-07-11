package com.example.barogo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drvr")
@Getter
@Setter
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drvr_seq_gen")
    @SequenceGenerator(name = "drvr_seq_gen", sequenceName = "DRIVER_ID", allocationSize = 1)
    @Column(name = "drvr_id")
    private Long driverId;

    // 미개발
}
