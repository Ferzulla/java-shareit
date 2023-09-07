package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemRequestDto {
    private Integer id;

    @NotBlank
    private String description;

    @NotNull
    private Long requestorId;
}