package space.nanobreaker.configuration.monolith.config;

import dev.thatwhichis.framework.ddd.EventDispatcher;
import io.quarkus.arc.DefaultBean;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.time.Clock;

@Dependent
public class Configuration {

    @Inject
    EventBus eventBus;

    @Produces
    @DefaultBean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Produces
    @DefaultBean
    public EventDispatcher eventDispatcher() {
        return new EventDispatcher(eventBus);
    }
}

