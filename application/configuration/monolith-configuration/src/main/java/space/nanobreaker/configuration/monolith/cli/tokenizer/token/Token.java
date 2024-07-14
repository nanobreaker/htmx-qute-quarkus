package space.nanobreaker.configuration.monolith.cli.tokenizer.token;

public sealed interface Token
        permits Arg, Cmd, Opt, Prog, Unk {

}