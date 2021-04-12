package exceptions;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;

public class UnknownAlgorithmException extends ImageWebException {
    private final String algoName;
    private final String algoTitle;
    public final HttpStatus status = HttpStatus.BAD_REQUEST;

    public UnknownAlgorithmException(String message, String algoName) {
        this(message, algoName, null);
    }

    public UnknownAlgorithmException(String message, String algoName, String algoTitle) {
        super(message);
        this.algoName = algoName;
        this.algoTitle = algoTitle;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode node = super.toJSON();
        node.put("name", this.algoName);
        node.put("title", this.algoTitle);
        return node;
    }
}
