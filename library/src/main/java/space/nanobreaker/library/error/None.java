package space.nanobreaker.library.error;

public record None() implements Error {

    @Override
    public String describe() {
        return "not found";
    }
}
