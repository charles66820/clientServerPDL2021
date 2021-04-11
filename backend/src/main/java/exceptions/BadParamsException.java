package exceptions;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import imageProcessing.AlgorithmArgs;

import java.util.HashMap;
import java.util.List;

public class BadParamsException extends ImageWebException {

    private List<AlgorithmArgs> badParamsList;

    private HashMap<String, Object> paramValue; //key = name of argument
    private boolean paramExist;
    private boolean paramsValid;

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

    public boolean areParamsValid() {
        return paramsValid;
    }

    public boolean doesParamExist() {
        return paramExist;
    }

    public void setParamsValid(boolean valid) {
        paramsValid = valid;
    }

    public void setParamExist(boolean exist) {
        paramExist = exist;
    }

    @Override
    public String toString() {
        String value = "";
        if (!paramExist) {
            value = "Among these parameters, one doesn't exist !";
        }
        if (!paramsValid) {
            value = "Parameters are invalid !";
        }
        return value;
    }

    @Override
    public ObjectNode toJSON() {
        super.setType("BadParamsException");

        ObjectNode node = super.toJSON();
        // List of bad parameters
        ArrayNode badParamsListNode = node.putArray("badParams");
        for(AlgorithmArgs param : badParamsList) {
            ObjectNode paramNode = super.getMapper().createObjectNode();
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
                paramNode.put("value", Integer.parseInt(this.paramValue.get(param.name).toString()));
                paramNode.put("min", param.min);
                paramNode.put("max", param.max);
            }
            // If parameter is a selector
            else if (param.type.equals("select")) {
                paramNode.put("value", this.paramValue.get(param.name).toString());
                // Display expected value for the selector
                ArrayNode expectedValueListNode = paramNode.putArray("expectedValue");
                for(AlgorithmArgs value : param.options) {
                    ObjectNode expectedValueNode = super.getMapper().createObjectNode();
                    expectedValueNode.put("name", value.name);
                    expectedValueNode.put("title", value.title);
                    expectedValueNode.put("type", value.type);
                    expectedValueNode.put("required", value.required);
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
