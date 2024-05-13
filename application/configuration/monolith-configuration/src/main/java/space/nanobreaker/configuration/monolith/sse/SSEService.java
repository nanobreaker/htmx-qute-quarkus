package space.nanobreaker.configuration.monolith.sse;

import jakarta.inject.Singleton;
import jakarta.ws.rs.sse.SseEventSink;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SSEService {

    private final ConcurrentHashMap<String, SseEventSink> sseEventSinks = new ConcurrentHashMap<>();

    public void register(String principal, SseEventSink eventSink) {
        sseEventSinks.put(principal, eventSink);
    }

    public Optional<SseEventSink> get(String principal) {
        final SseEventSink sseEventSink = sseEventSinks.get(principal);
        if (sseEventSink == null || sseEventSink.isClosed()) {
            return Optional.empty();
        }

        return Optional.of(sseEventSink);
    }
}
