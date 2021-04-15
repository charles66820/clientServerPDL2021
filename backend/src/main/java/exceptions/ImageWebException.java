package exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;

public abstract class ImageWebException extends Exception {
    public final ObjectMapper mapper = new ObjectMapper();
    private final String message;
    protected HttpStatus status;

    public ImageWebException(String message) {
        super(message);
        this.message = message;
    }

    public HttpStatus getStatus() {
        return this.status;
    }

    /**
     * Create a node in JSON with all informations of the exception
     *
     * @return node in JSON
     */
    public ObjectNode toJSON() {
        ObjectNode node = this.mapper.createObjectNode();
        node.put("type", getClass().getSimpleName());
        node.put("message", this.message);

        return node;
    }
}
