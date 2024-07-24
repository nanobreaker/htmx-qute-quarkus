package space.nanobreaker.configuration.monolith.cli.command;

import space.nanobreaker.library.Error;

public sealed interface CmdErr extends Error {

    // @formatter:off
    record CreationFailed(String description) implements CmdErr { }
    // @formatter:on

}