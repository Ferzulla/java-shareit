package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.validator.ValidStartEndDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@ValidStartEndDate
@AllArgsConstructor
@FieldDefaults(level=AccessLevel.PRIVATE)
public class BookingClientDto {
    @Positive
    @NotNull
     Integer itemId;

    @NotNull
     LocalDateTime start;

    @NotNull
     LocalDateTime end;
}
