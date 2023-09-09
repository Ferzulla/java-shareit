package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServerDto {
     Integer id;

    @NotBlank
     String name;

    @NotBlank
     String description;

    @NotNull
     Boolean available;
     BookingItemDto lastBooking;
     BookingItemDto nextBooking;
     List<CommentServerDto> comments;
}
