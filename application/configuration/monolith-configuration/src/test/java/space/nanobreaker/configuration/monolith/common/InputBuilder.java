package space.nanobreaker.configuration.monolith.common;

public class InputBuilder {

    private final StringBuffer input;

    public InputBuilder(final String initial) {
        this.input = new StringBuffer(initial).append(" ");
    }

    public InputBuilder append(String input) {
        this.input.append(input);
        this.input.append(" ");
        return this;
    }

    public String build() {
        return input.toString().trim();
    }
}
