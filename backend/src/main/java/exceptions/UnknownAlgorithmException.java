package exceptions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;

public class UnknownAlgorithmException extends ImageWebException {
    private String algoName;
    public final HttpStatus status = HttpStatus.BAD_REQUEST;

    public UnknownAlgorithmException(String message) {
        super(message);
    }

    public UnknownAlgorithmException(String message, String algoName) {
        super(message);
        this.algoName = algoName;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode node = super.toJSON();
        node.put("name", this.algoName);
        return node;
    }
}
