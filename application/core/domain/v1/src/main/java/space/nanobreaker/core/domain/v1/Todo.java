package space.nanobreaker.core.domain.v1;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo {

    @EmbeddedId
    private TodoId id;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

}
