package space.nanobreaker.configuration.monolith.services.sse;

// @formatter:off
public sealed interface SseEvent
        permits
        SseEvent.AllTodosDeleted,
        SseEvent.TodoCreated,
        SseEvent.TodoDeleted,
        SseEvent.TodoUpdated {

    record TodoCreated(String upn, String sid, String html) implements SseEvent { }
    record TodoUpdated(String upn, String sid, Integer id) implements SseEvent { }
    record TodoDeleted(String upn, String sid, Integer id) implements SseEvent { }
    record AllTodosDeleted(String upn, String sid) implements SseEvent { }
}
// @formatter:on