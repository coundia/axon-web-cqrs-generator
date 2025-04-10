package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommandGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public CommandGeneratorService(
            TemplateEngine templateEngine,
            FileWriterService fileWriterService,
            GeneratorProperties generatorProperties
    ) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String outputDir) {
        List<String> commandTypes = List.of("Create", "Update", "Delete");

        for (String type : commandTypes) {
            generateCommand(type, definition, outputDir);
        }
    }

    private void generateCommand(String prefix, EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getCommandPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("commandType", prefix);
        context.put("entity", definition.getName());

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        context.put("imports", imports);

        context.put("isDeleted", prefix.equalsIgnoreCase("Delete"));
        context.put("isCreated", prefix.equalsIgnoreCase("Create"));

        var fieldFiles = definition.fieldFiles();
        context.put("hasFiles", !fieldFiles.isEmpty());
        if (!fieldFiles.isEmpty()) {
            context.put("fieldFiles", FieldTransformer.transform(fieldFiles, definition.getName()));
        }

        context.put("name", prefix + definition.getName());
        String content = templateEngine.render("application/command.mustache", context);
        fileWriterService.write(outputDir, prefix + definition.getName() + "Command.java", content);
    }
}