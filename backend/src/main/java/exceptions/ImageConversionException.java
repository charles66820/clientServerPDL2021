package exceptions;

import org.springframework.http.HttpStatus;

public class ImageConversionException extends ImageWebException {
    public final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ImageConversionException(String message) {
        super(message);
    }

}
