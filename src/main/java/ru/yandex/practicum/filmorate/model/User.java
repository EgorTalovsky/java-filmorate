package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.enums.Friendship;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private long userId;
    private final String email;
    private String name;
    private final String login;
    private final LocalDate birthday;
    private Set<Long> friends = new HashSet<>();
    private Map<Long, Friendship> incomingRequests = new HashMap<>();
    private Map<Long, Friendship> outgoingRequests = new HashMap<>();

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriend(Long friendId) {
        getFriends().add(friendId);
    }
}
