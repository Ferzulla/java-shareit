package ru.practicum.shareit.item.comment.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentServerDto {
      Integer id;
      Long authorId;
      String authorName;
      String text;
      LocalDateTime created;
}
