package space.nanobreaker.configuration.monolith.cli.parser.token;

public sealed interface Token
        permits Argument, Command, Option, Program, Unknown {

}