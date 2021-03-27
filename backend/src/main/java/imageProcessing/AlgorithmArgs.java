package imageProcessing;

public class AlgorithmArgs {
    public final String name;
    public final String title;
    public final String type;
    public final long min;
    public final long max;
    public final boolean required;

    public AlgorithmArgs(String name, String title, String type, long min, long max, boolean required) {
        this.name = name;
        this.title = title;
        this.type = type;
        this.min = min;
        this.max = max;
        this.required = required;
    }
}
