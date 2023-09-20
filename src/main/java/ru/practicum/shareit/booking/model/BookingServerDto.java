package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServerDto {
      Integer id;
      UserDto booker;
      ItemServerDto item;
      LocalDateTime start;
      LocalDateTime end;
      Status status;
}
