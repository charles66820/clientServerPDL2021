package imageProcessing;

public class AlgorithmArgs {
    public final String name;
    public final String title;
    public final long min;
    public final long max;
    public final boolean required;

    public AlgorithmArgs(String name, String title, long min, long max, boolean required) {
        this.name = name;
        this.title = title;
        this.min = min;
        this.max = max;
        this.required = required;
    }
}
