package space.nanobreaker.app.bootstrap;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.VertxContextSupport;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import space.nanobreaker.user.core.domain.UserConfiguration;

@ApplicationScoped
public class AppLifeCycleBean {

    private static final Logger LOG = Logger.getLogger(AppLifeCycleBean.class);

    private static final String projectTitle = """
                        
                        
            ██╗  ██╗████████╗███╗   ███╗██╗  ██╗         ██████╗ ██╗   ██╗████████╗███████╗         ██████╗ ██╗   ██╗ █████╗ ██████╗ ██╗  ██╗██╗   ██╗███████╗
            ██║  ██║╚══██╔══╝████╗ ████║╚██╗██╔╝        ██╔═══██╗██║   ██║╚══██╔══╝██╔════╝        ██╔═══██╗██║   ██║██╔══██╗██╔══██╗██║ ██╔╝██║   ██║██╔════╝
            ███████║   ██║   ██╔████╔██║ ╚███╔╝         ██║   ██║██║   ██║   ██║   █████╗          ██║   ██║██║   ██║███████║██████╔╝█████╔╝ ██║   ██║███████╗
            ██╔══██║   ██║   ██║╚██╔╝██║ ██╔██╗         ██║▄▄ ██║██║   ██║   ██║   ██╔══╝          ██║▄▄ ██║██║   ██║██╔══██║██╔══██╗██╔═██╗ ██║   ██║╚════██║
            ██║  ██║   ██║   ██║ ╚═╝ ██║██╔╝ ██╗        ╚██████╔╝╚██████╔╝   ██║   ███████╗        ╚██████╔╝╚██████╔╝██║  ██║██║  ██║██║  ██╗╚██████╔╝███████║
            ╚═╝  ╚═╝   ╚═╝   ╚═╝     ╚═╝╚═╝  ╚═╝         ╚══▀▀═╝  ╚═════╝    ╚═╝   ╚══════╝         ╚══▀▀═╝  ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝
                        
            """;

    private static final String projectDescription = """
                        
            ██████╗ ███████╗ █████╗  ██████╗████████╗██╗██╗   ██╗███████╗    ████████╗ ██████╗       ██████╗  ██████╗      █████╗ ██████╗ ██████╗ ██╗     ██╗ ██████╗ █████╗ ████████╗██╗ ██████╗ ███╗   ██╗
            ██╔══██╗██╔════╝██╔══██╗██╔════╝╚══██╔══╝██║██║   ██║██╔════╝    ╚══██╔══╝██╔═══██╗      ██╔══██╗██╔═══██╗    ██╔══██╗██╔══██╗██╔══██╗██║     ██║██╔════╝██╔══██╗╚══██╔══╝██║██╔═══██╗████╗  ██║
            ██████╔╝█████╗  ███████║██║        ██║   ██║██║   ██║█████╗         ██║   ██║   ██║█████╗██║  ██║██║   ██║    ███████║██████╔╝██████╔╝██║     ██║██║     ███████║   ██║   ██║██║   ██║██╔██╗ ██║
            ██╔══██╗██╔══╝  ██╔══██║██║        ██║   ██║╚██╗ ██╔╝██╔══╝         ██║   ██║   ██║╚════╝██║  ██║██║   ██║    ██╔══██║██╔═══╝ ██╔═══╝ ██║     ██║██║     ██╔══██║   ██║   ██║██║   ██║██║╚██╗██║
            ██║  ██║███████╗██║  ██║╚██████╗   ██║   ██║ ╚████╔╝ ███████╗       ██║   ╚██████╔╝      ██████╔╝╚██████╔╝    ██║  ██║██║     ██║     ███████╗██║╚██████╗██║  ██║   ██║   ██║╚██████╔╝██║ ╚████║
            ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝ ╚═════╝   ╚═╝   ╚═╝  ╚═══╝  ╚══════╝       ╚═╝    ╚═════╝       ╚═════╝  ╚═════╝     ╚═╝  ╚═╝╚═╝     ╚═╝     ╚══════╝╚═╝ ╚═════╝╚═╝  ╚═╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝

            """;

    @Inject
    UserConfiguration userConfiguration;

    void onStart(@Observes StartupEvent startupEvent) throws Throwable {
        LOG.info(projectTitle.concat(projectDescription));
        VertxContextSupport.subscribeAndAwait(() -> userConfiguration.execute());
    }

    void onStop(@Observes ShutdownEvent shutdownEvent) {
        LOG.info("The application is stopping...");
    }

}
