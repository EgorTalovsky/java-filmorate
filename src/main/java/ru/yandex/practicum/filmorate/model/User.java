package ru.yandex.practicum.filmorate.model;

import lombok.*;


import java.time.LocalDate;

@Data
public class User {
    private long id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;

    public User() {
    }

    public User( String name, String login, String email, LocalDate birthday) {
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }

    public User(long id, String login, String name, String email, LocalDate birthday) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
    }
}
