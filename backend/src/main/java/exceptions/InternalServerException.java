package exceptions;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ImageWebException {

    public InternalServerException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
