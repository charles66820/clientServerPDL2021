package exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;

public class ImageWebException extends Exception {
    private ObjectMapper mapper = new ObjectMapper();
    private String type;
    private String message;
    public final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ImageWebException(String message) {
        super(message);
        this.message = message;
    }

    public ImageWebException() {
        super();
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Create a node in JSON with all informations of the exception
     * @return node in JSON
     */
    public ObjectNode toJSON() {
        ObjectNode node = this.mapper.createObjectNode();
        node.put("Type", this.type);
        node.put("Message", this.message);

        return node;
    }
}
