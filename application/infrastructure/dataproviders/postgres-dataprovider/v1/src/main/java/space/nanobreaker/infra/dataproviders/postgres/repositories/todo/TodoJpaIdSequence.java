package space.nanobreaker.infra.dataproviders.postgres.repositories.todo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TodoJpaIdSequence {

    @Id
    private String username;
    private Integer seq;

    public void setSeq(Integer id) {
        this.seq = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSeq() {
        return seq;
    }

    public String getUsername() {
        return username;
    }
}