package exceptions;

public class BadParamsException extends Exception {

    private boolean paramExist;
    private boolean paramsValid;

    public BadParamsException(String message) {
        super(message);
    }

    public BadParamsException() {
        super();
    }

    public boolean areParamsValid() {
        return paramsValid;
    }

    public boolean paramDoesExist() {
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
}
