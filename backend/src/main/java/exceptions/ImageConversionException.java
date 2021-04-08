package exceptions;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class ImageConversionException extends ImageWebException {

    public ImageConversionException(String message) {
        super(message);
    }

    @Override
    public ObjectNode toJSON() {
        super.setType("ImageConversionException");
        return super.toJSON();
    }
}
