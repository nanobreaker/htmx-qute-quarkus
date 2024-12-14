package space.nanobreaker.core.domain.v1.calendar;

import java.util.Objects;

public class CalendarId {

    private Integer id;

    private String username;

    public CalendarId(
            final Integer id,
            final String username
    ) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(username);

        this.id = id;
        this.username = username;
    }

    public CalendarId() {
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}