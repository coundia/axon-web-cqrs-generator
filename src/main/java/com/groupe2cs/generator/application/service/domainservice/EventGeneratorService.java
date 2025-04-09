package com.groupe2cs.generator.application.service.domainservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public EventGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        List<String> eventTypes = List.of("Created", "Updated", "Deleted");

        for (String type : eventTypes) {
            generateEvent(definition, baseDir, type);
        }
    }

    private void generateEvent(EntityDefinition definition, String baseDir, String eventType) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getEventPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("eventType", eventType);

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        context.put("imports", imports);

        context.put("isDeleted", eventType.equalsIgnoreCase("Deleted"));

        String content = templateEngine.render("domain/event.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + eventType + "Event.java", content);
    }
}
