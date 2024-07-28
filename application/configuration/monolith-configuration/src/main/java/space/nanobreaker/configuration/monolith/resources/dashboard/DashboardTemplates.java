
package space.nanobreaker.configuration.monolith.resources.dashboard;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "dashboard", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class DashboardTemplates {

    static native TemplateInstance dashboard(
            String applicationName,
            String applicationVersion,
            String username
    );

}
