package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_STATE_VALUE = "ALL";

    @PostMapping
    public BookingServerDto addBooking(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId, @RequestBody @Valid BookingClientDto bookingClientDto) {
        log.info("Принят запрос на добавление бронирования");
        return bookingService.addBooking(userId, bookingClientDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingServerDto approveBooking(
            @RequestHeader(USER_ID_HEADER) @Positive Long ownerId,
            @PathVariable @Positive Integer bookingId,
            @RequestParam @NotNull Boolean approved) {
        log.info(String.format("Принят запрос на подтверждение или отклонение бронирования ID %s", bookingId));
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingServerDto getBooking(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId, @PathVariable @Positive Integer bookingId) {
        log.info(String.format("Принят запрос на получение данных бронирования ID %s от пользователя ID %s",
                bookingId, userId));
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingServerDto> getUserBookings(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestParam (defaultValue = DEFAULT_STATE_VALUE) State state) {
        log.info(String.format("Принят запрос на получение списка бронирований пользователя ID %s", userId));
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingServerDto> getItemBookings(
            @RequestHeader(USER_ID_HEADER) @Positive Long userId,
            @RequestParam (defaultValue = DEFAULT_STATE_VALUE) State state) {
        log.info(String.format("Принят запрос на получение списка бронирований для всех вещей пользователя ID %s",
                userId));
        return bookingService.getItemBookings(userId, state);
    }
}
