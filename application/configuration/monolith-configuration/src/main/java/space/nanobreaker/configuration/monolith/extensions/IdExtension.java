package space.nanobreaker.configuration.monolith.extensions;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension(namespace = "id")
public class IdExtension {

    public static String format(final Integer id) {
        return String.format("%03d", id);
    }
}
