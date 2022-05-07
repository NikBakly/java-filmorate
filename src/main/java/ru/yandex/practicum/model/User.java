package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class User {
    private final Long id;
    @Email
    private final String email;
    @NotBlank
    @NotNull
    private final String login;
    private final String name;
    @NotNull
    private final LocalDate birthday;

    @Builder.Default
    private final Set<Long> friends = new HashSet<>();


    public Set<Long> getFriends() {
        return friends;
    }
}
