package exceptions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import imageProcessing.AlgorithmArgs;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

public class BadParamsException extends ImageWebException {

    private List<AlgorithmArgs> badParamsList;
    private HashMap<String, Object> paramValue; //key = name of argument
    public final HttpStatus status = HttpStatus.BAD_REQUEST;

    public BadParamsException(String message, List<AlgorithmArgs> badParamsList, HashMap<String, Object> paramValue) {
        super(message);
        this.badParamsList = badParamsList;
        this.paramValue = paramValue;
    }

    public BadParamsException(String message) {
        super(message);
    }

    public BadParamsException() {
        super();
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode node = super.toJSON();
        // If we don't have a list of parameters
        if (badParamsList == null) {
            return node;
        }
        // List of bad parameters
        ArrayNode badParamsListNode = node.putArray("badParams");
        for (AlgorithmArgs param : badParamsList) {
            ObjectNode paramNode = super.mapper.createObjectNode();
            paramNode.put("name", param.name);
            paramNode.put("title", param.getTitle());
            paramNode.put("type", param.type);
            paramNode.put("required", param.required);

            // If parameter does not have a value
            Object value;
            if (this.paramValue.containsKey(param.name)) {
                value = this.paramValue.get(param.name);
            }

            // If parameter is a number
            if (param.type.equals("number")) {
                if (value == null) paramNode.put("value", "null");
                else if (value instanceof Integer)
                    paramNode.put("value", (Integer) value);
                else paramNode.put("value", value.toString());

                paramNode.put("min", param.min);
                paramNode.put("max", param.max);
            }
            // If parameter is a selector
            else if (param.type.equals("select")) {
                if (value == null) paramNode.put("value", "null");
                else paramNode.put("value", value.toString());

                // Display expected value for the selector
                ArrayNode expectedValueListNode = paramNode.putArray("expectedValue");
                for (AlgorithmArgs option : param.options) {
                    ObjectNode expectedValueNode = super.mapper.createObjectNode();
                    expectedValueNode.put("name", option.name);
                    expectedValueNode.put("title", option.getTitle());
                    expectedValueListNode.add(expectedValueNode);
                }
            }
            // If parameter has another type
            else {
                if (value == null) paramNode.put("value", "null");
                else paramNode.put("value", value.toString());
            }
            badParamsListNode.add(paramNode);
        }
        return node;
    }
}
