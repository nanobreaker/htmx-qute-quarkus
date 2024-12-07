package space.nanobreaker.configuration.monolith.services.tokenizer;

public sealed interface Token
        permits
        Token.Prog,
        Token.Cmd,
        Token.SubCmd,
        Token.Arg,
        Token.Opt,
        Token.Unk {

    // @formatter:off
    sealed interface Prog extends Token {
        record Help()       implements Prog { }
        record Todo()       implements Prog { }
        record User()       implements Prog { }
        record Calendar()   implements Prog { }
    }

    sealed interface Cmd extends Token {
        record Help()   implements Cmd { }
        record Create() implements Cmd { }
        record List()   implements Cmd { }
        record Update() implements Cmd { }
        record Delete() implements Cmd { }
        record Show()   implements Cmd { }
    }

    sealed interface SubCmd extends Token {
        record Help()   implements SubCmd { }
        record All()    implements SubCmd { }
    }

    record Arg(String value) implements Token { }

    sealed interface Opt extends Token {
        record Filters(String value)        implements Opt { }
        record Title(String value)          implements Opt { }
        record Description(String value)    implements Opt { }
        record Start(String value)          implements Opt { }
        record End(String value)            implements Opt { }
    }

    record Unk(String value) implements Token { }
    // @formatter:on
}