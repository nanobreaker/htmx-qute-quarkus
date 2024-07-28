package space.nanobreaker.configuration.monolith.services.analyzer;

import space.nanobreaker.library.Error;

public sealed interface AnalyzerErr extends Error {

    // @formatter:off
    record NotSupportedOperation() implements AnalyzerErr { }
    // @formatter:on

}