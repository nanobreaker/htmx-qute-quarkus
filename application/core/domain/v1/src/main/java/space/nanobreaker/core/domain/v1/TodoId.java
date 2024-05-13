package space.nanobreaker.core.domain.v1;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoId implements Serializable {

    @GeneratedValue
    private Long id;

    private String username;

}
