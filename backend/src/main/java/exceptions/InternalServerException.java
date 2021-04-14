package exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ImageWebException {
    public final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public InternalServerException(String message) {
        super(message);
    }

}
