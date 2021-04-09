package imageProcessing;

import java.util.List;

public class AlgorithmArgs {
    public final String name;
    public String title;
    public final String type;
    public final long min;
    public final long max;
    public final boolean required;
    public final List<AlgorithmArgs> options;

    public AlgorithmArgs(String name, String title, String type, long min, long max, boolean required, List<AlgorithmArgs> options) {
        this.name = name;
        this.title = title;
        this.type = type;
        this.min = min;
        this.max = max;
        this.required = required;
        this.options = options;
    }

    public AlgorithmArgs(String name, String title, String type, long min, long max, boolean required) {
        this(name, title, type, min, max, required, null);
    }

    public AlgorithmArgs(String name, String title, String type, boolean required, List<AlgorithmArgs> options) {
        this(name, title, type, 0, 0, required, options);
    }

    public AlgorithmArgs(String name, String title, String type, boolean required) {
        this(name, title, type, 0, 0, required, null);
    }

}
