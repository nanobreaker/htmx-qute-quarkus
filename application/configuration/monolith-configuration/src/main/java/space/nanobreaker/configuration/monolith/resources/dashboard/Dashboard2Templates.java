
package space.nanobreaker.configuration.monolith.resources.dashboard;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "dashboard2", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class Dashboard2Templates {

    static native TemplateInstance dashboard2(
            String applicationName,
            String applicationVersion,
            String username
    );

}
