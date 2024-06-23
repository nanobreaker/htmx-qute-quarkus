package space.nanobreaker.configuration.monolith.cli.parser;

import java.util.List;
import java.util.StringTokenizer;

public class Tokenizer {

    // @formatter:off

    sealed interface Token { }

    sealed interface Program extends Token {
        record TodoProgram() implements Program { }
        record UserProgram() implements Program { }
        record CalendarProgram() implements Program { }
    }

    sealed interface Command extends Token { }
    sealed interface Todo extends Command {
        record Create() implements Todo { }
        record Update() implements Todo { }
        record List() implements Todo { }
        record Delete() implements Todo { }
    }
    sealed interface User extends Command {
        record Show() implements User { }
    }
    sealed interface Calendar extends Command {
        record Show() implements Calendar { }
    }

    sealed interface Option extends Token { }
    sealed interface TodoOption extends Option {
        record Title(String title) implements TodoOption { }
        record Description(String description) implements TodoOption { }
        record Start(String date) implements TodoOption { }
        record End(String date) implements TodoOption { }
    }

    sealed interface Argument extends Token { }
    sealed interface TodoArgument extends Argument {
        record Argument(String id) implements TodoArgument { }
    }

    // @formatter:on

    public List<Token> tokenize(final String input) {
        // finite state machine logic here
        final char[] chars = input.toCharArray();

        // as example what we want to return at the end
        return List.of(
                new Program.TodoProgram(),
                new Todo.Create(),
                new TodoOption.Title("yoga"),
                new TodoOption.Start("20/06/2024")
        );
    }

}
