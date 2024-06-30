package space.nanobreaker.configuration.monolith.cli.parser;

import jakarta.enterprise.context.ApplicationScoped;
import space.nanobreaker.configuration.monolith.cli.parser.token.*;
import space.nanobreaker.configuration.monolith.extension.Result;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.SequencedCollection;

@ApplicationScoped
public class Tokenizer {

    sealed interface State {
        record New() implements State {
        }

        record StringLiteral() implements State {
        }

        record OptionStart() implements State {
        }

        record ArgumentValue() implements State {
        }

        record TitleOption() implements State {
        }

        record TitleOptionValue() implements State {
        }

        record DescriptionOption() implements State {
        }

        record DescriptionOptionValue() implements State {
        }

        record StartOption() implements State {
        }

        record StartOptionValue() implements State {
        }

        record EndOption() implements State {
        }

        record EndOptionValue() implements State {
        }

        record UknownOption() implements State {
        }

        record UknownOptionValue() implements State {
        }

        record FinalizeToken() implements State {
        }

        record Exit() implements State {
        }
    }

    public Result<SequencedCollection<Token>, TokenizerError> tokenize(final String input) {
        if (input == null || input.isEmpty())
            return Result.err(new TokenizerError("no input"));

        final SequencedCollection<Token> tokens = new LinkedList<>();

        // variables to keep track of state
        State stateNow = new State.New();
        State stateNext = new State.New();

        StringBuilder currentTokenString = new StringBuilder();
        Token currentToken = null;

        boolean done = false;

        final StringCharacterIterator iterator = new StringCharacterIterator(input);
        char character = iterator.first();

        while (done == false) {

            switch (stateNow) {
                case State.New _ -> {
                    currentTokenString = new StringBuilder();
                    if (Character.isAlphabetic(character)) {
                        currentTokenString.append(character);
                        stateNext = new State.StringLiteral();
                        character = iterator.next();
                    }
                    if (Character.isWhitespace(character)) {
                        character = iterator.next();
                    }
                    if (character == '-') {
                        stateNext = new State.OptionStart();
                        character = iterator.next();
                    }
                    if (character == '"') {
                        stateNext = new State.ArgumentValue();
                        character = iterator.next();
                    }
                    if (character == CharacterIterator.DONE) {
                        stateNext = new State.Exit();
                    }
                }

                case State.StringLiteral _ -> {
                    if (Character.isAlphabetic(character)) {
                        currentTokenString.append(character);
                        character = iterator.next();
                    }
                    if (Character.isWhitespace(character) || character == CharacterIterator.DONE) {
                        currentToken = switch (currentTokenString.toString()) {
                            case "todo" -> new Program.Todo();
                            case "user" -> new Program.User();
                            case "calendar" -> new Program.Calendar();
                            case "update" -> new Command.Update();
                            case "create" -> new Command.Create();
                            case "list" -> new Command.List();
                            case "delete" -> new Command.Delete();
                            case "show" -> new Command.Show();
                            default -> new Unknown(currentTokenString.toString());
                        };

                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.ArgumentValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unknown(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Argument(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.OptionStart _ -> {
                    if (Character.isAlphabetic(character)) {
                        switch (character) {
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
                                stateNext = new State.UknownOption();
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

                case State.DescriptionOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.DescriptionOptionValue();
                    }
                }

                case State.StartOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.StartOptionValue();
                    }
                }

                case State.EndOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.EndOptionValue();
                    }
                }

                case State.UknownOption _ -> {
                    if (character == '"') {
                        character = iterator.next();
                        stateNext = new State.UknownOptionValue();
                    }
                }

                case State.TitleOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unknown(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Option.Title(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.DescriptionOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unknown(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Option.Description(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.StartOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unknown(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (Character.isDigit(character) || character == '/') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else if (character == '"') {
                        currentToken = new Option.Start(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.EndOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unknown(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (Character.isDigit(character) || character == '/') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else if (character == '"') {
                        currentToken = new Option.End(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    }
                }

                case State.UknownOptionValue _ -> {
                    if (character == CharacterIterator.DONE) {
                        currentToken = new Unknown(currentTokenString.toString());
                        stateNext = new State.FinalizeToken();
                    } else if (character != '"') {
                        currentTokenString.append(character);
                        character = iterator.next();
                    } else {
                        currentToken = new Unknown(currentTokenString.toString());
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
