package space.nanobreaker.core.domain.v1.todo;

import space.nanobreaker.library.Option;

import java.time.LocalDateTime;

public record TodoUpdatePayload(
        Option<String> someTitle,
        Option<String> someDescription,
        Option<LocalDateTime> someStart,
        Option<LocalDateTime> someEnd
) {
}
