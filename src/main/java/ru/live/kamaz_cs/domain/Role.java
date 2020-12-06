package ru.live.kamaz_cs.domain;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name(); // name() - это строковое представление USER
    }
}
