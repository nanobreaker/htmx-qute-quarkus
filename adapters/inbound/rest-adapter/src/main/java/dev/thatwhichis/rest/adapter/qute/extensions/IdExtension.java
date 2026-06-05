package dev.thatwhichis.rest.adapter.qute.extensions;

import io.quarkus.qute.TemplateExtension;

@TemplateExtension(namespace = "id")
public class IdExtension {

    public static String format(final Integer id) {
        return String.format("%03d", id);
    }
}
