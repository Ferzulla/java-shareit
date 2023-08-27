package ru.practicum.shareit.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.EntityNotFoundException;
import ru.practicum.shareit.user.exception.EmailConflictException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(EmailConflictException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        return new ErrorResponse("Адрес почты уже используется", e.getMessage());
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(EntityNotFoundException e) {
        log.debug("Получен статус 404 Not Found {}", e.getMessage(), e);
        return new ErrorResponse("Не найдено", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        log.debug("Получен статус 400 Bad Request  {}", e.getMessage(), e);
        return new ErrorResponse("Ошибка валидации 400:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(Throwable e) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR  {}", e.getMessage(), e);
        return new ErrorResponse("Получен статус 500 INTERNAL_SERVER_ERROR", e.getMessage());
    }
}
