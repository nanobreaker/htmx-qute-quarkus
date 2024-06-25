package space.nanobreaker.configuration.monolith.cli.parser;

import space.nanobreaker.configuration.monolith.extension.Result;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SequencedCollection;

public class Tokenizer {

    // @formatter:off

    sealed interface Token { }

    record New() implements Token { }
    record Unknown() implements Token { }
    record Complete() implements Token { }

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

    sealed interface State {
        record New() implements State { }
        record StringLiteral() implements State { }
        record DashLiteral() implements State { }
        record TitleOption() implements State { }
        record TitleOptionValue() implements State { }
        record NumericLiteral() implements State { }
    }

    record TokenizerState(State state) { };

    // @formatter:on

    public Result<SequencedCollection<Token>, CommandParser.TokenizerError> tokenize(final String input) {
        if (input == null || input.isEmpty())
            return Result.err(new CommandParser.TokenizerError("no input"));

        final SequencedCollection<Token> tokens = new LinkedList<>();

        // variables to keep track of state
        State stateNow = new State.New();
        State stateNext = new State.New();

        StringBuffer currentTokenString = new StringBuffer();
        Token currentToken = null;

        final StringCharacterIterator iterator = new StringCharacterIterator(input);

        char character = iterator.first();
        do {

            switch (stateNow) {
                case State.New _ -> {
                    currentTokenString = new StringBuffer();
                    if (Character.isAlphabetic(character)) {
                        currentTokenString.append(character);
                        stateNext = new State.StringLiteral();
                        character = iterator.next();
                    }
                    if (character == '-') {
                        stateNext = new State.DashLiteral();
                        character = iterator.next();
                    }
                }

                case State.StringLiteral _ -> {
                    if (Character.isAlphabetic(character)) {
                        currentTokenString.append(character);
                        character = iterator.next();
                    }
                    if (Character.isWhitespace(character) || character == CharacterIterator.DONE) {
                        currentToken = switch (currentTokenString.toString()) {
                            case "todo" -> new Program.TodoProgram();
                            case "user" -> new Program.UserProgram();
                            case "calendar" -> new Program.CalendarProgram();
                            case "create" -> new Todo.Create();
                            case "update" -> new Todo.Update();
                            case "list" -> new Todo.List();
                            case "delete" -> new Todo.Delete();
                            case "show" -> new User.Show();
                            default -> new Unknown();
                        };

                        tokens.add(currentToken);
                        stateNext = new State.New();
                        character = iterator.next();
                    }
                }

                case State.DashLiteral _ -> {
                    if (Character.isAlphabetic(character)) {
                        switch (character) {
                            case 't' -> {
                                stateNext = new State.TitleOption();
                                character = iterator.next();
                            }
                        }
                    }
                }

                case State.TitleOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.TitleOptionValue();
                    }
                }

                case State.TitleOptionValue _ -> {
                    if (Character.isAlphabetic(character)) {
                        currentTokenString.append(character);
                        character = iterator.next();
                    }
                    if (character == '"') {
                        currentToken = new TodoOption.Title(currentTokenString.toString());
                        tokens.add(currentToken);
                        stateNext = new State.New();
                        character = iterator.next();
                    }
                }

                default -> System.out.println("nothing");
            }

            stateNow = stateNext;
        } while (character != CharacterIterator.DONE);

        // as example what we want to return at the end
        return Result.ok(tokens);
    }

}
