package space.nanobreaker.configuration.monolith.services.command;

import space.nanobreaker.core.domain.v1.todo.TodoId;

import java.time.LocalDateTime;
import java.util.Set;

public record UpdateTodoCmd(
        Set<TodoId> ids,
        String description,
        LocalDateTime start,
        LocalDateTime end
) implements TodoCmd {

    public static String help() {
        return """
                  usage: todo update "id" [options]
                  \s
                  argument:
                  "id"              "string"           id (title)
                  \s
                  options:
                  -d --description  "string"           description
                  -s --start        "dd/mm/yy hh:mm"   start date(time),
                  -e --end          "dd/mm/yy hh:mm"   end date(time)
                  \s
                  examples:
                  todo update "yoga" -d"new description"
                  todo update "vacation" -s"23/11" -e"26/11"
                  todo update "walk with doggy" -s"12:00" -e"15:00"
                """;
    }
}