package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

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
    private final String name;
    @NotBlank
    @NotNull
    private final String login;
    @Email
    private final String email;
    @NotNull
    private final LocalDate birthday;
    @Builder.Default
    private final Set<Long> friends = new HashSet<>();
}
