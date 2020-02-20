package abid.fullpdfsearch.service;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebsiteRenderer {

    public String render(String template, Map<String, Object> data) {
        try {
            final Map<String, Object> context = new HashMap<>();
            context.putAll(data);

            final PebbleEngine engine = new PebbleEngine.Builder().build();
            final PebbleTemplate compiledTemplate = engine.getTemplate(template);
            final Writer writer = new StringWriter();
            compiledTemplate.evaluate(writer, context);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to render page", e);
        }
    }
}
