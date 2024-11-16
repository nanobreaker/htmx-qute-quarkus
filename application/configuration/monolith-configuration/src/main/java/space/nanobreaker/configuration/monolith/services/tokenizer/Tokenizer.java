package space.nanobreaker.configuration.monolith.services.tokenizer;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.configuration.monolith.services.tokenizer.token.*;
import space.nanobreaker.library.error.Error;
import io.github.dcadea.jresult.Result;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.SequencedCollection;


@ApplicationScoped
public class Tokenizer {

    // @formatter:off
    sealed interface State {
        record New() implements State { }
        record StringLiteral() implements State { }
        record OptionStart() implements State { }
        record ArgumentValue() implements State { }
        record FilterOption() implements State { }
        record FilterOptionValue() implements State { }
        record TitleOption() implements State { }
        record TitleOptionValue() implements State { }
        record DescriptionOption() implements State { }
        record DescriptionOptionValue() implements State { }
        record StartOption() implements State { }
        record StartOptionValue() implements State { }
        record EndOption() implements State { }
        record EndOptionValue() implements State { }
        record UnknownOption() implements State { }
        record UnknownOptionValue() implements State { }
        record FinalizeToken() implements State { }
        record Exit() implements State { }
    }
    // @formatter:on

    @WithSpan("tokenizeInputString")
    public Result<SequencedCollection<Token>, Error> tokenize(final String input) {
        if (input == null || input.isEmpty()) {
            return Result.err(new TokenizerError.EmptyInput());
        }

        final SequencedCollection<Token> tokens = new LinkedList<>();

        State stateNow = new State.New();
        State stateNext = new State.New();

        StringBuffer currentTokenString = new StringBuffer();
        Token currentToken = null;
        boolean done = false;

        final StringCharacterIterator iterator = new StringCharacterIterator(input);
        char character = iterator.first();

        while (done == false) {
            switch (stateNow) {

                case State.New _ -> {
                    currentTokenString = new StringBuffer();
                    if (Character.isLetterOrDigit(character)) {
                        currentTokenString.append(character);
                        stateNext = new State.StringLiteral();
                        character = iterator.next();
                    } else if (Character.isWhitespace(character)) {
                        character = iterator.next();
                    } else if (character == '-') {
                        stateNext = new State.OptionStart();
                        character = iterator.next();
                    } else if (character == '"') {
                        stateNext = new State.ArgumentValue();
                        character = iterator.next();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.StringLiteral _-> {
                    if (Character.isLetterOrDigit(character)) {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else if (Character.isWhitespace(character) || character == CharacterIterator.DONE) {
                        currentToken = switch (currentTokenString.toString()) {
                            case "todo" -> new Prog.Todo();
                            case "user" -> new Prog.User();
                            case "calendar" -> new Prog.Calendar();
                            case "update" -> new Cmd.Update();
                            case "create" -> new Cmd.Create();
                            case "list" -> new Cmd.List();
                            case "delete" -> new Cmd.Delete();
                            case "show" -> new Cmd.Show();
                            default -> new Unk(currentTokenString.toString());
                        };

                        stateNext = new State.FinalizeToken();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.ArgumentValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Arg(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.OptionStart _ -> {
                    if (Character.isAlphabetic(character)) {
                        switch (character) {
                            case 'f' -> {
                                stateNext = new State.FilterOption();
                                character = iterator.next();
                            }
                            case 't' -> {
                                stateNext = new State.TitleOption();
                                character = iterator.next();
                            }
                            case 'd' -> {
                                stateNext = new State.DescriptionOption();
                                character = iterator.next();
                            }
                            case 's' -> {
                                stateNext = new State.StartOption();
                                character = iterator.next();
                            }
                            case 'e' -> {
                                stateNext = new State.EndOption();
                                character = iterator.next();
                            }
                            default -> {
                                stateNext = new State.UnknownOption();
                                character = iterator.next();
                            }
                        }
                    } else if (character == ' ') {
                        character = iterator.next();
                    } else if (character == '"') {
                        stateNext = new State.UnknownOptionValue();
                        character = iterator.next();
                    }
                }

                case State.FilterOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.FilterOptionValue();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.TitleOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.TitleOptionValue();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.DescriptionOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.DescriptionOptionValue();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.StartOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.StartOptionValue();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.EndOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.EndOptionValue();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.UnknownOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.UnknownOptionValue();
                    } else if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    } else {
                        character = iterator.next();
                    }
                }

                case State.FilterOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Opt.Filters(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.TitleOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Opt.Title(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.DescriptionOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Opt.Description(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.StartOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character == '"') {
                        currentToken = new Opt.Start(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else {
                        currentTokenString.append(character);
                        character = iterator.next();
                    }
                }

                case State.EndOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character == '"') {
                        currentToken = new Opt.End(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else {
                        currentTokenString.append(character);
                        character = iterator.next();
                    }
                }

                case State.UnknownOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Unk(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.FinalizeToken _ -> {
                    tokens.add(currentToken);
                    stateNext = new State.New();
                    character = iterator.next();
                }

                case State.Exit _ -> done = true;
            }

            stateNow = stateNext;
        }

        return Result.ok(tokens);
    }

}