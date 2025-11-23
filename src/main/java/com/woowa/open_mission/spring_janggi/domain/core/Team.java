package com.woowa.open_mission.spring_janggi.domain.core;

import lombok.Getter;

@Getter
public enum Team {
    CHO("초", "Green"),
    HAN("한", "Red");

    private final String name;
    private final String color;

    Team(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Team opposite() {
        return this == CHO ? HAN : CHO;
    }
}