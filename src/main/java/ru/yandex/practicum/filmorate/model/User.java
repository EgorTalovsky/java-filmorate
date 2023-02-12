package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class User {
    private final int id;
    private String email;
    private String login;
    private final String name;
    private final String birthday;
}
