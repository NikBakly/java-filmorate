package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class Film {
    private Long id;
    @NotEmpty
    private final String name;
    private final String description;
    private final MPA mpa; // возрастное ограничение фильма
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    private final int duration;
    @Builder.Default
    private Long rate = 0L; // количество лайков

    public void addRate() {
        ++rate;
    }

    public void deleteRate() {
        --rate;
    }
}
