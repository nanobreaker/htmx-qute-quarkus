package dev.thatwhichis.rest.adapter.qute.templates;

import dev.thatwhichis.core.domain.calendar.Calendar;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;

import java.time.ZoneId;

@CheckedTemplate(basePath = "calendar", defaultName = CheckedTemplate.HYPHENATED_ELEMENT_NAME)
public class CalendarTemplates {

    public static native TemplateInstance calendar(Calendar calendar, ZoneId zone);
}
