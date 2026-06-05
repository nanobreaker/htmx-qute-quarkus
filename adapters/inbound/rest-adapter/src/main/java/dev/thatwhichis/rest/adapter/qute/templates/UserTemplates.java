package dev.thatwhichis.rest.adapter.qute.templates;

import dev.thatwhichis.core.domain.user.User;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.time.ZoneId;

@CheckedTemplate(basePath = "user", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class UserTemplates {

    public static native TemplateInstance user(User user, ZoneId zone);
}
