package com.groupe2cs.generator.application.service.testservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ControllerAllIntegrationTestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public ControllerAllIntegrationTestGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {

        List<String> classNames = List.of(
                "CreateControllerIntegrationTest"
        );

        classNames.forEach(
                p -> generateControllerTest(baseDir, p,definition)
        );
    }

    private void generateControllerTest(String baseDir, String className,EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();
        String fullPath = baseDir + "/" + generatorProperties.getControllerPackage();

        String packageName = Utils.getTestPackage(fullPath);
        context.put("package", packageName);
        String outputDir = Utils.getTestDir(fullPath);
        String entity = definition.getName();
        context.put("className", className);
        context.put("name", entity);
        context.put("nameLower", entity.toLowerCase());

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getSharedPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        context.put("imports", imports);

        String content = templateEngine.render("tests/" + className.toLowerCase() + ".mustache", context);

        fileWriterService.write(outputDir, entity+className + ".java", content);
    }

}
