
package space.nanobreaker.configuration.microservice.user.resources.dashboard;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

@CheckedTemplate(basePath = "v1/dashboard", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class DashboardTemplates {

  static native TemplateInstance dashboard();

}
