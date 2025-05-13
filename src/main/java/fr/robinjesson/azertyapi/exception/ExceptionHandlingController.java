package fr.robinjesson.azertyapi.exception;


import org.springframework.web.bind.annotation.ExceptionHandler;

public class ExceptionHandlingController {

    @ExceptionHandler({BadRequestException.class})
    public String handleException(final BadRequestException badRequestException) {
        return badRequestException.getMessage();
    }
}
