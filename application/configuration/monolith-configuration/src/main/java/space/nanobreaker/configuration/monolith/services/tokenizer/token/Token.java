package space.nanobreaker.configuration.monolith.services.tokenizer.token;

public sealed interface Token
        permits Arg, Cmd, Opt, Prog, Unk {

}