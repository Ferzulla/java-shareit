package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestor;
    private LocalDateTime created;
}
