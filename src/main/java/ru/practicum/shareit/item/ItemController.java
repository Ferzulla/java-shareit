package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemServerDto> getAllUserItems(@RequestHeader(USER_ID_HEADER) @Positive Long ownerId) {
        log.info(String.format("Принят запрос на получение списка всех вещей пользователя ID %s", ownerId));
        return itemService.getAllUserItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemServerDto getItem(@RequestHeader(USER_ID_HEADER) @Positive Long userId,
                                 @PathVariable @Positive Integer itemId) {
        log.info(String.format("Принят запрос на получение вещи ID %s", itemId));
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemServerDto> getItemsBySearch(@RequestParam String text) {
        log.info("Принят запрос на получение списка вещей по поисковой строке \"" + text + "\"");
        return itemService.getItemsBySearch(text);
    }

    @PostMapping
    public ItemServerDto addItem(@RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
                                 @RequestBody @Valid ItemClientDto itemDto) {
        log.info(String.format("Принят запрос на добавление новой вещи пользователя ID %s", ownerId));
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemServerDto editItem(
            @RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
            @PathVariable @Positive Integer itemId,
            @RequestBody ItemClientDto itemDto) {
        log.info(String.format("Принят запрос на редактирование данных вещи ID %s пользователя ID %s",itemId, ownerId));
        return itemService.editItem(ownerId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentServerDto addComment(@RequestHeader(USER_ID_HEADER) @Positive Long authorId,
                                       @PathVariable @Positive Integer itemId,
                                       @RequestBody @Valid CommentClientDto commentDto) {
        log.info(String.format("Принят запрос на добавление комментария к вещи ID %s", itemId));
        return itemService.addComment(authorId, itemId, commentDto);
    }
}
