package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.library.option.Option;

import java.time.LocalDateTime;
import java.util.Set;

public sealed interface Command
        permits
        Command.Help,
        Command.Todo,
        Command.Calendar,
        Command.User {

    // @formatter:off
    record Help() implements Command { }

    sealed interface Todo extends Command {
        record Help() implements Todo { }

        sealed interface Create extends Todo {
            record Help()                   implements Create { }
            record Default(
                    String title,
                    Option<String> description,
                    Option<LocalDateTime> start,
                    Option<LocalDateTime> end
            ) implements Create { }
        }

        sealed interface List extends Todo {
            record Help()                                                   implements List { }
            record All()                                                    implements List { }
            record ByIds(Set<Integer> ids)                                  implements List { }
            record ByFilters(Set<String> filters)                           implements List { }
            record ByIdsAndFilters(Set<Integer> ids, Set<String> filters)   implements List { }
        }

        sealed interface Update extends Todo {
            record Help()                                             implements Update { }
            record ByIds(Set<Integer> ids, Payload payload)           implements Update { }
            record ByFilters(Set<String> filters, Payload payload)    implements Update { }
            record ByIdsAndFilters(
                    Set<Integer> ids,
                    Set<String> filters,
                    Payload payload
            ) implements Update { }

            record Payload(
                    Option<String> title,
                    Option<String> description,
                    Option<LocalDateTime> start,
                    Option<LocalDateTime> end
            ) { }
        }

        sealed interface Delete extends Todo {
            record Help()                    implements Delete { }
            record All()                     implements Delete { }
            record ByIds(Set<Integer> ids)   implements Delete { }
        }
    }

    sealed interface Calendar extends Command {
        record Help() implements Calendar { }
        record Show() implements Calendar { }
    }

    sealed interface User extends Command {
        record Help() implements User { }
        record Show() implements User { }
    }
    // @formatter:on

    static String help() {
        return """
                  \s
                  usage
                  \s
                    <program> <command> "<args>" [options]
                  \s
                  programs
                  \s
                    todo          manage todos
                    calendar      manage calendar
                    user          manage user
                  \s
                """;
    }
}