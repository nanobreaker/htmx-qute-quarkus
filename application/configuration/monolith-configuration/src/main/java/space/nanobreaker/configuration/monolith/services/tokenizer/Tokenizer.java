package space.nanobreaker.configuration.monolith.services.tokenizer;

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.constraints.NotNull;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.SequencedCollection;

@ApplicationScoped
public class Tokenizer {

    // @formatter:off
    sealed interface State {
        State BEGIN_TOKEN = new State.BeginToken();
        State KEYWORD = new State.Keyword();
        State OPTION = new State.Option();
        State TEXT = new State.Text();
        State EXIT = new State.Exit();

        record BeginToken()             implements State { }
        record EndToken(Token token)    implements State { }
        record Keyword()                implements State { }
        record Option()                 implements State { }
        record Text()                   implements State { }
        record Exit()                   implements State { }
    }

    enum CharCode {
        LETTER_OR_DIGIT,
        WHITESPACE,
        QUOTATION_MARK,
        DASH,
        EMPTY,
        NOT_SUPPORTED;

        public static CharCode from(final Character character){
            if (Character.isLetterOrDigit(character)) {
                return LETTER_OR_DIGIT;
            } else if (Character.isWhitespace(character)) {
                return WHITESPACE;
            } else if (character == '"') {
                return QUOTATION_MARK;
            } else if (character == '-') {
                return DASH;
            } else if (character == CharacterIterator.DONE) {
                return EMPTY;
            } else {
                return NOT_SUPPORTED;
            }
        }
    }
    // @formatter:on

    @WithSpan("tokenizeInputString")
    public SequencedCollection<Token> tokenize(@NotNull final String source) {
        SequencedCollection<Token> tokens = new LinkedList<>();
        var chat_iter = new StringCharacterIterator(source);
        var char_buff = new StringBuffer();
        var character = chat_iter.first();
        var state = (State) new State.BeginToken();
        var stop = false;

        while (!stop) {
            state = switch (state) {
                case State.BeginToken _ -> switch (CharCode.from(character)) {
                    case CharCode.LETTER_OR_DIGIT -> {
                        char_buff.append(character);
                        character = chat_iter.next();
                        yield State.KEYWORD;
                    }
                    case CharCode.WHITESPACE -> {
                        character = chat_iter.next();
                        yield State.BEGIN_TOKEN;
                    }
                    case CharCode.QUOTATION_MARK -> {
                        character = chat_iter.next();
                        yield State.TEXT;
                    }
                    case CharCode.DASH -> {
                        character = chat_iter.next();
                        yield State.OPTION;
                    }
                    default -> State.EXIT;
                };
                case State.Keyword _ -> switch (CharCode.from(character)) {
                    case CharCode.LETTER_OR_DIGIT -> {
                        char_buff.append(character);
                        character = chat_iter.next();
                        yield State.KEYWORD;
                    }
                    default -> {
                        var keyword = KEYWORD.from(char_buff.toString());
                        yield new State.EndToken(new Token.Keyword(keyword));
                    }
                };
                case State.Text _ -> switch (CharCode.from(character)) {
                    case CharCode.QUOTATION_MARK -> {
                        character = chat_iter.next();
                        var text = char_buff.toString();
                        yield new State.EndToken(new Token.Text(text));
                    }
                    case CharCode.EMPTY -> {
                        var text = char_buff.toString();
                        yield new State.EndToken(new Token.Unknown(text));
                    }
                    default -> {
                        char_buff.append(character);
                        character = chat_iter.next();
                        yield State.TEXT;
                    }
                };
                case State.Option _ -> switch (CharCode.from(character)) {
                    case CharCode.QUOTATION_MARK, CharCode.EMPTY -> {
                        var option = OPTION.from(char_buff.toString());
                        yield new State.EndToken(new Token.Option(option));
                    }
                    case CharCode.WHITESPACE -> {
                        character = chat_iter.next();
                        yield State.OPTION;
                    }
                    default -> {
                        char_buff.append(character);
                        character = chat_iter.next();
                        yield State.OPTION;
                    }
                };
                case State.EndToken(var token) -> {
                    tokens.add(token);
                    char_buff.delete(0, char_buff.length());
                    yield State.BEGIN_TOKEN;
                }
                case State.Exit _ -> {
                    stop = true;
                    yield State.EXIT;
                }
            };
        }

        return tokens;
    }
}