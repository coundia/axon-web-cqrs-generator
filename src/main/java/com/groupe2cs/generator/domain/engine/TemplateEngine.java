package com.groupe2cs.generator.domain.engine;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplateEngine {

    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory("templates");

    public String render(String templateName, Map<String, Object> context) {
        Mustache mustache = mustacheFactory.compile(templateName);
        StringWriter writer = new StringWriter();
        mustache.execute(writer, context);
        return writer.toString();
    }
}
