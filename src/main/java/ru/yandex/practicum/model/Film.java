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
    private final String genre;
    private final String description;
    private final String MPA; // возрастное ограничение фильма
    @NotNull
    private final LocalDate releaseDate;
    @NotNull
    private final int duration;

    @Builder.Default
    private Long numberOfLikes = 0L; // количество лайков

    public void addRate() {
        ++numberOfLikes;
    }

    public void deleteRate() {
        --numberOfLikes;
    }
}
