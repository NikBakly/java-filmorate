package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    private final int id;
    @Email
    private final String email;
    @NotBlank
    @NotNull
    private final String login;
    private final String name;
    @NotNull
    private final LocalDate birthday;
}
