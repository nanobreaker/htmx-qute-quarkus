package space.nanobreaker.configuration.monolith.config;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import space.nanobreaker.core.usecases.repositories.v1.TodoRepository;
import space.nanobreaker.infra.dataproviders.postgres.repositories.PostgresPanacheTodoRepository;

@Dependent
public class TodoRepositoryConfig {

    @Produces
    @DefaultBean
    public TodoRepository postgresTodoRepository() {
        return new PostgresPanacheTodoRepository();
    }

}

