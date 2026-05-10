package java.dev.thatwhichis.rest.adapter.extensions;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension(namespace = "id")
public class IdExtension {

    public static String format(final Integer id) {
        return String.format("%03d", id);
    }
}
