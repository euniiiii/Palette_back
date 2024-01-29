package com.project.palette.entity;

import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum Field {
    FRONT_END("프론트엔드"), BACK_END("백엔드"), PLANNER("기획");

    private final String field;

}
