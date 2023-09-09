package ru.practicum.shareit.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@FieldDefaults(level= AccessLevel.PRIVATE)
public class ItemRequestDto {
     Integer id;

    @NotBlank
     String description;

    @NotNull
     Long requestorId;
}