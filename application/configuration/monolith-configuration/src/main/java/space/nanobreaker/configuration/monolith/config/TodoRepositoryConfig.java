package space.nanobreaker.configuration.monolith.config;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import space.nanobreaker.core.domain.v1.todo.TodoIdSequenceGenerator;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.infra.dataproviders.postgres.repositories.todo.TodoIdPostgresSequenceGenerator;
import space.nanobreaker.infra.dataproviders.postgres.repositories.todo.TodoJpaPostgresRepository;

@Dependent
public class TodoRepositoryConfig {

    @Produces
    @DefaultBean
    public TodoRepository postgresTodoRepository() {
        return new TodoJpaPostgresRepository();
    }

    @Produces
    @DefaultBean
    public TodoIdSequenceGenerator postgresTodoIdSequenceGenerator() {
        return new TodoIdPostgresSequenceGenerator();
    }

}

