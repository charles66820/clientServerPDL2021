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

    public BadParamsException(String message, List<AlgorithmArgs> badParamsList, HashMap<String , Object> paramValue) {
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
        if(badParamsList == null) {
            return node;
        }
        // List of bad parameters
        ArrayNode badParamsListNode = node.putArray("badParams");
        for(AlgorithmArgs param : badParamsList) {
            ObjectNode paramNode = super.mapper.createObjectNode();
            paramNode.put("name", param.name);
            paramNode.put("title", param.title);
            paramNode.put("type", param.type);
            paramNode.put("required", param.required);
            // If parameter does not have a value
            if (!this.paramValue.containsKey(param.name)) {
                paramNode.put("value", "null");
            }
            // If parameter is a number
            else if (param.type.equals("number")) {
                if (this.paramValue.get(param.name) == null) {
                    paramNode.put("value", "null");
                    paramNode.put("min", param.min);
                    paramNode.put("max", param.max);
                } else {
                    try {
                        paramNode.put("value", Integer.parseInt(this.paramValue.get(param.name).toString()));
                        paramNode.put("min", param.min);
                        paramNode.put("max", param.max);
                    } catch (NumberFormatException nbr) {
                        paramNode.put("value", this.paramValue.get(param.name).toString());
                        paramNode.put("min", param.min);
                        paramNode.put("max", param.max);
                    }
                }
            }
            // If parameter is a selector
            else if (param.type.equals("select")) {
                paramNode.put("value", this.paramValue.get(param.name).toString());
                // Display expected value for the selector
                ArrayNode expectedValueListNode = paramNode.putArray("expectedValue");
                for(AlgorithmArgs value : param.options) {
                    ObjectNode expectedValueNode = super.mapper.createObjectNode();
                    expectedValueNode.put("name", value.name);
                    expectedValueNode.put("title", value.title);
                    expectedValueListNode.add(expectedValueNode);
                }
            }
            // If parameter has another type
            else {
                paramNode.put("value", this.paramValue.get(param.name).toString());
            }
            badParamsListNode.add(paramNode);
        }
        return node;
    }
}
