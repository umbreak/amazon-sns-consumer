package komoot.notification.rest;


import komoot.notification.model.ErrorResponse;


public class NotificationException extends RuntimeException{
    private final ErrorResponse.Error error;
    public NotificationException(String message, ErrorResponse.Error error) {
        super(message);
        this.error=error;

    }

    public NotificationException(ErrorResponse.Error error) {
        this.error=error;
    }

    public ErrorResponse.Error getError() {
        return error;
    }
}
