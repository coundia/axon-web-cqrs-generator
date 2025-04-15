package com.groupe2cs.generator.application.service.testservice;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandApplicationTestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public CommandApplicationTestGeneratorService(
            TemplateEngine templateEngine,
            FileWriterService fileWriterService,
            GeneratorProperties generatorProperties
    ) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        String packagePath = baseDir + "/" + generatorProperties.getApplicationPackage();
        String packageName = Utils.getTestPackage(packagePath);
        String outputDir = Utils.getTestDir(packagePath);

        List<Map<String, Object>> fields = FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName());

        Map<String, Object> context = new HashMap<>();
        context.put("package", packageName);
        context.put("className", "Create" + definition.getName() + "CommandTest");
        context.put("commandName", "Create" + definition.getName() + "Command");
        context.put("eventName", definition.getName() + "CreatedEvent");
        context.put("fields", fields);
        context.put("entity", definition.getName());

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getSharedPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getEventPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        imports.add("org.axonframework.commandhandling.gateway.CommandGateway");
        imports.add("com.fasterxml.jackson.databind.ObjectMapper");

        context.put("imports", imports);

        String content = templateEngine.render("tests/CommandApplicationTests.mustache", context);
        fileWriterService.write(outputDir, context.get("className") + ".java", content);
    }
}
