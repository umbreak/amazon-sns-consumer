package komoot.notification.model;

import lombok.Data;

@Data
public class ErrorResponse {
    private Error error;

    private String message;

    public static enum Error{
        NOT_DEFINED, UNMARSHALLING, SUBSCRIBING_PROCESS
    }

    public ErrorResponse() {
    }
    public ErrorResponse(Error error, String message) {
        this.error = error;
        this.message = message;
    }

}
