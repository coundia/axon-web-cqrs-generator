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
public class AggregateGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public AggregateGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getDomainPackage();
        context.put("package", Utils.getPackage(outputDir));

        var fields = definition.getFields();
        var idField = fields.stream()
                .filter(f -> f.getName().equalsIgnoreCase("id"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No field named 'id' found"));

        context.put("aggregateIdField", idField.getName());
        context.put("aggregateIdType", definition.getName() + "Id");

        context.put("fields", FieldTransformer.transform(fields, definition.getName()));
        context.put("imports", buildImports(baseDir));

        String content = templateEngine.render("domain/aggregate.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Aggregate.java", content);
    }

    private Set<String> buildImports(String baseDir) {
        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEventPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*");

        imports.addAll(List.of(
                "org.axonframework.commandhandling.CommandHandler",
                "org.axonframework.eventsourcing.EventSourcingHandler",
                "org.axonframework.modelling.command.AggregateIdentifier",
                "org.axonframework.spring.stereotype.Aggregate",
                "static org.axonframework.modelling.command.AggregateLifecycle.apply"
        ));

        return imports;
    }
}
