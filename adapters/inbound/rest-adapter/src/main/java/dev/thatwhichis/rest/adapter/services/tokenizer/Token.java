package dev.thatwhichis.rest.adapter.services.tokenizer;

public sealed interface Token {

    // @formatter:off
    record Keyword(KEYWORD keyword)     implements Token { }
    record Text(String text)            implements Token { }
    record Option(OPTION option)        implements Token { }
    record Unknown(String text)        implements Token { }
    // @formatter:on
}