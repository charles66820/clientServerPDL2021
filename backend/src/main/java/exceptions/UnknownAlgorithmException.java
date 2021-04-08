package exceptions;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UnknownAlgorithmException extends ImageWebException {
    private String algoName;

    public UnknownAlgorithmException(String message) {
        super(message);
    }

    public UnknownAlgorithmException(String message, String algoName) {
        super(message);
        this.algoName = algoName;
    }

    @Override
    public ObjectNode toJSON() {
        super.setType("UnknownAlgorithmException");
        ObjectNode node = super.toJSON();
        node.put("Name", this.algoName);
        return node;
    }
}
