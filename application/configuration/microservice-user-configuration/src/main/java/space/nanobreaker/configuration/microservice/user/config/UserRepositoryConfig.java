package space.nanobreaker.configuration.microservice.user.config;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import space.nanobreaker.core.usecases.repositories.v1.UserRepository;
import space.nanobreaker.infra.dataproviders.postgres.repositories.PostgresPanacheUserRepository;

@Dependent
public class UserRepositoryConfig {

    @Produces
    @DefaultBean
    public UserRepository postgresTodoRepository() {
        return new PostgresPanacheUserRepository();
    }

}
