package com.project.palette.domain;

import jakarta.persistence.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
public class Heart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

}
