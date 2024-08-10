package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class TodoJpaId implements Serializable {

    private Integer id;
    private String username;

    public TodoJpaId(
            final Integer id,
            final String username
    ) {
        this.id = id;
        this.username = username;
    }

    public TodoJpaId() {
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