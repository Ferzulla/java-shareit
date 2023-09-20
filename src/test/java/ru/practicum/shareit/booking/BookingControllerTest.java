package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private final BookingServerDto bookingServerDto = new BookingServerDto(
            1,
            new UserDto(1L, "testUser", "test@mail.ru"),
            new ItemServerDto(1, "testItem", "test", true, null, null, null, new ArrayList<>()),
            LocalDateTime.of(2024, 1, 1, 0, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0, 0),
            Status.WAITING
    );

    private final BookingClientDto bookingClientDto = new BookingClientDto(
            1,
            LocalDateTime.of(2024, 1, 1, 0, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0, 0)
    );

    @SneakyThrows
    @Test
    void addBooking_whenInvoked_thenStatusOkAndBookingDto() {
        when(bookingService.addBooking(anyLong(), any())).thenReturn(bookingServerDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingServerDto)));

        verify(bookingService, times(1)).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addBooking_whenNoSuchUserFound_thenStatusNotFound() {
        when(bookingService.addBooking(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3L)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addBooking_whenInvalidDto_thenStatusBadRequest() {
        bookingClientDto.setEnd(bookingClientDto.getStart());

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -999L})
    void addBooking_whenInvalidUserId_thenStatusBadRequest(long userId) {
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void approveBooking_whenInvoked_thenStatusOkAndBookingDto() {
        when(bookingService.approveBooking(anyLong(), anyInt(), anyBoolean())).thenReturn(bookingServerDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingServerDto)));

        verify(bookingService, times(1)).approveBooking(anyLong(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approveBooking_whenNoSuchEntityFound_thenStatusNotFound() {
        when(bookingService.approveBooking(anyLong(), anyInt(), anyBoolean())).thenThrow(EntityNotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approveBooking(anyLong(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void approveBooking_whenInvalidId_thenStatusBadRequest(long userId, int bookingId) {
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(anyLong(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getBooking_whenInvoked_thenStatusOkAndBookingDto() {
        when(bookingService.getBooking(anyLong(), anyInt())).thenReturn(bookingServerDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingServerDto)));

        verify(bookingService, times(1)).getBooking(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getBooking_whenNoSuchEntityFound() {
        when(bookingService.getBooking(anyLong(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBooking(anyLong(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void getBooking_whenInvalidId_thenStatusBadRequest(long userId, int bookingId) {
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBooking(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getUserBookings_whenInvoked_thenStatusOkAndBookingDtoList() {
        when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingServerDto));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingServerDto))));

        verify(bookingService, times(1)).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getUserBookings_whenNoSuchUserFound_thenStatusNotFound() {
        when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"-1,1,1", "1,-1,1", "1,1,-1"})
    void getUserBookings_whenInvalidHeaderOrParameter_thenStatusBadRequest(long userId, int from, int size) {
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemBookings_whenInvoked_thenStatusOkAndBookingDtoList() {
        when(bookingService.getItemBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingServerDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingServerDto))));

        verify(bookingService, times(1)).getItemBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemBookings_whenNoSuchUserFound_thenStatusNotFound() {
        when(bookingService.getItemBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getItemBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"-1,1,1", "1,-1,1", "1,1,-1"})
    void getItemBookings_whenInvalidHeaderOrParameter_thenStatusBadRequest(long userId, int from, int size) {
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getItemBookings(anyLong(), any(), anyInt(), anyInt());
    }
}