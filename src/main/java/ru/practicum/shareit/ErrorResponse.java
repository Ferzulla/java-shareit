package ru.practicum.shareit;

public class ErrorResponse {
    private final String error;
    //private final String description;

   /* public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    */

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

   /* public String getDescription() {
        return description;
    }

    */
}
