package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    @NotNull
    private final int id;
    @NotEmpty
    private final String name;
    private final String description;
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    private final Duration duration;
}
