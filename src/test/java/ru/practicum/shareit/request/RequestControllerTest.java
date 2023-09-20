package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    private final RequestClientDto requestClientDto = new RequestClientDto("test");
    private final RequestServerDto requestServerDto
            = new RequestServerDto(1, "test", LocalDateTime.of(2000, 1, 1, 0, 0, 0), Collections.emptyList());

    @SneakyThrows
    @Test
    void addRequest_whenInvoked_thenStatusOkAndRequestDto() {
        when(requestService.addRequest(anyLong(), any())).thenReturn(requestServerDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestServerDto)));

        verify(requestService, times(1)).addRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addRequest_whenInvalidDto_thenStatusBadRequest() {
        requestClientDto.setDescription(null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(requestClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).addRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequests_whenInvoked_thenStatusOkAndRequestDtoList() {
        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(requestServerDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestServerDto))));

        verify(requestService, times(1)).getUserRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getUserRequests_whenNoSuchUserFound_thenStatusNotFound() {
        when(requestService.getUserRequests(anyLong())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getUserRequests(anyLong());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -999L})
    void getUserRequests_whenInvalidId_thenStatusBadRequest(long userId) {
        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getUserRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getOtherRequests_whenInvoked_thenStatusOkAndRequestDtoList() {
        when(requestService.getOtherRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestServerDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestServerDto))));

        verify(requestService, times(1)).getOtherRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getOtherRequests_whenNoSuchUserFound_thenStatusOkAndRequestDtoList() {
        when(requestService.getOtherRequests(anyLong(), anyInt(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getOtherRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -999L})
    void getOtherRequests_whenInvalidId_thenStatusBadRequest(long userId) {
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getOtherRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequest_whenInvoked_thenStatusOkAndRequestDto() {
        when(requestService.getRequest(anyLong(), anyInt())).thenReturn(requestServerDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestServerDto)));

        verify(requestService, times(1)).getRequest(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequest_whenNoSuchUserOrRequestFound_thenStatusNotFound() {
        when(requestService.getRequest(anyLong(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequest(anyLong(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void getRequest_whenOneOrBothIdsInvalid_thenStatusBadRequest(long userId, int requestId) {
        mvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getRequest(anyLong(), anyInt());
    }
}