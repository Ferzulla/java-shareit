package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final ItemClientDto itemClientDto = new ItemClientDto("test", "test description", true, null);
    private final ItemServerDto itemServerDto = new ItemServerDto(1, "test", "test description", true, null, null, null, new ArrayList<>());
    private final CommentClientDto commentClientDto = new CommentClientDto("test comment");
    private final CommentServerDto commentServerDto = new CommentServerDto(1, 1L, "test name", "test comment", LocalDateTime.of(2000, 1, 1, 0, 0, 0));

    @SneakyThrows
    @Test
    void getAllUserItems_whenInvoked_thenStatusOkAndItemDtoList() {
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemServerDto));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemServerDto))));

        verify(itemService, times(1)).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllUserItems_whenNoSuchUserFound_thenStatusNotFound() {
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 3L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"-1,1,1", "1,-1,1", "1,1,-1"})
    void getAllUserItems_whenInvalidHeaderOrParameter_thenStatusBadRequest(long userId, int from, int size) {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItem_whenInvoked_thenStatusOkAndItemDto() {
        when(itemService.getItem(anyLong(), anyInt())).thenReturn(itemServerDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemServerDto)));

        verify(itemService, times(1)).getItem(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItem_whenNoSuchEntityFound_thenStatusNotFound() {
        when(itemService.getItem(anyLong(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/items/{itemId}", 2)
                        .header("X-Sharer-User-Id", 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItem(anyLong(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void getItem_whenInvalidId_thenStatusBadRequest(long userId, int itemId) {
        mvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItem(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemsBySearch_whenInvoked_thenStatusOkAndItemDtoList() {
        when(itemService.getItemsBySearch(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemServerDto));

        mvc.perform(get("/items/search")
                        .param("text", "search")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemServerDto))));

        verify(itemService, times(1)).getItemsBySearch(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"null,1,1", "search,-1,1", "search,1,0"}) // TODO: как написать null?
    void getItemsBySearch_whenInvalidParameter_thenStatusBadRequest(String text, int from, int size) {
        if (text.equals("null")) {
            text = null;
        }

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getItemsBySearch(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void addItem_whenInvoked_thenStatusOkAndItemDto() {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemServerDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemServerDto)));

        verify(itemService, times(1)).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addItem_whenNoSuchUserFound_thenStatusNotFound() {
        when(itemService.addItem(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).addItem(anyLong(), any());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -999L})
    void addItem_whenInvalidUserId_thenStatusBadRequest(long userId) {
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addItem_whenInvalidDto_thenStatusBadRequest() {
        itemClientDto.setDescription("");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void editItem_whenInvoked_thenStatusOkAndItemDto() {
        when(itemService.editItem(anyLong(), anyInt(), any())).thenReturn(itemServerDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemServerDto)));

        verify(itemService, times(1)).editItem(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void editItem_whenNoSuchEntityFound_thenStatusNotFound() {
        when(itemService.editItem(anyLong(), anyInt(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).editItem(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void editItem_whenInvalidDto_thenStatusBadRequest() {
        itemClientDto.setName(null);

        when(itemService.editItem(anyLong(), anyInt(), any())).thenThrow(ConstraintViolationException.class);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, times(1)).editItem(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void editItem_whenInvalidId_thenStatusBadRequest(long userId, int itemId) {
        mvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).editItem(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void addComment_whenInvoked_thenStatusOkAndCommentDto() {
        when(itemService.addComment(anyLong(), anyInt(), any())).thenReturn(commentServerDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentServerDto)));

        verify(itemService, times(1)).addComment(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void addComment_whenNoSuchEntityFound_thenStatusNotFound() {
        when(itemService.addComment(anyLong(), anyInt(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).addComment(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void addComment_whenInvalidDto_thenStatusBadRequest() {
        commentClientDto.setText("");

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void addComment_whenInvalidId_thenStatusBadRequest(long userId, int itemId) {
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(commentClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(anyLong(), anyInt(), any());
    }
}