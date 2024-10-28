package space.nanobreaker.configuration.monolith.services.analyzer;

import space.nanobreaker.library.error.Error;

public sealed interface AnalyzerErr extends Error {

    // @formatter:off
    record NotSupportedOperation() implements AnalyzerErr { }
    // @formatter:on

    @Override
    default String describe() {
        return switch (this) {
            case NotSupportedOperation _ -> "not supported operations";
        };
    }
}