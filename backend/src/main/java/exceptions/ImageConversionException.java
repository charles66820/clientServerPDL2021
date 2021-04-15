package exceptions;

import org.springframework.http.HttpStatus;

public class ImageConversionException extends ImageWebException {

    public ImageConversionException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
