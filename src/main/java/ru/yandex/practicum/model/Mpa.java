package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {
    private int id;
    private String name;
}