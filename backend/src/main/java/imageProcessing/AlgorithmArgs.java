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
    public final List<AlgorithmArgs> options;

    public AlgorithmArgs(String name, String type, long min, long max, boolean required, List<AlgorithmArgs> options) {
        this.name = name;
        this.type = type;
        this.min = min;
        this.max = max;
        this.required = required;
        this.options = options;
    }

    public AlgorithmArgs(String name, String type, long min, long max, boolean required) {
        this(name, type, min, max, required, null);
    }

    public AlgorithmArgs(String name, String type, boolean required, List<AlgorithmArgs> options) {
        this(name, type, 0, 0, required, options);
    }

    public AlgorithmArgs(String name, String type, boolean required) {
        this(name, type, 0, 0, required, null);
    }

    public String getTitle() {
        String local = Middleware.getLocale().getLanguage();
        return titles.containsKey(local)? titles.get(local) : titles.get(Middleware.defaultLocale.getLanguage());
    }

}
