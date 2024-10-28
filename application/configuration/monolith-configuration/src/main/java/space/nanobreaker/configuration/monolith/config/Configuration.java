package space.nanobreaker.configuration.monolith.config;

import io.quarkus.arc.DefaultBean;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import space.nanobreaker.core.domain.v1.todo.TodoIdSequenceGenerator;
import space.nanobreaker.core.domain.v1.todo.TodoRepository;
import space.nanobreaker.ddd.EventDispatcher;
import space.nanobreaker.infra.dataproviders.postgres.repositories.todo.TodoIdPostgresSequenceGenerator;
import space.nanobreaker.infra.dataproviders.postgres.repositories.todo.TodoJpaPostgresRepository;

@Dependent
public class Configuration {

    @Inject
    EventBus eventBus;

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

    @Produces
    @DefaultBean
    public EventDispatcher eventDispatcher() {
        return new EventDispatcher(eventBus);
    }
}

