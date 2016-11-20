package komoot.notification;

import komoot.notification.model.ErrorResponse;
import komoot.notification.rest.NotificationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ErrorsHandling {
    @ResponseBody
    @ExceptionHandler(NotificationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ErrorResponse genericErrorHandler(NotificationException ex) {
        return new ErrorResponse(ex.getError(), ex.getMessage());
    }
}

