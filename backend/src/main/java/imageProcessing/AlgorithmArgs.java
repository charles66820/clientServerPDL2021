package imageProcessing;

import pdl.backend.Middleware;

import java.util.HashMap;
import java.util.List;

public class AlgorithmArgs {
    public final String name;
    public final HashMap<String, String> titles = new HashMap<>();
    public final String type;
    public final long min;
    public final long max;
    public final boolean required;
    public final float precision;
    public final List<AlgorithmArgs> options;

    public AlgorithmArgs(String name, String type, long min, long max, boolean required, float precision, List<AlgorithmArgs> options) {
        this.name = name;
        this.type = type;
        this.min = min;
        this.max = max;
        this.required = required;
        this.precision = precision;
        this.options = options;
    }

    public AlgorithmArgs(String name, String type, long min, long max, boolean required, float precision) {
        this(name, type, min, max, required, precision,null);
    }

    public AlgorithmArgs(String name, String type, boolean required, List<AlgorithmArgs> options) {
        this(name, type, 0, 0, required, 0, options);
    }

    public AlgorithmArgs(String name, String type, boolean required) {
        this(name, type, 0, 0, required, 0, null);
    }

    public String getTitle() {
        String requestLocale = Middleware.getLocale().getLanguage();
        return titles.containsKey(requestLocale) ?
                titles.get(requestLocale) :
                titles.getOrDefault(Middleware.defaultLocale.getLanguage(), "Undefined title");
    }

}
