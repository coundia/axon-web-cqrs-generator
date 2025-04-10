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
                "CreateControllerIntegrationTest",
                "UpdateControllerIntegrationTest",
                "DeleteControllerIntegrationTest",
                "FindAllControllerIntegrationTest",
                "FindByIdControllerIntegrationTest",
                "Fixtures"
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
        context.put("entity", entity);
        context.put("nameLower", entity.toLowerCase());

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        Set<String> imports = new LinkedHashSet<>();

        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getSharedPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*");
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*");

        context.put("imports", imports);
        context.put("base", Utils.getTestPackage(baseDir));
        context.put("vo", Utils.getPackage(generatorProperties.getVoPackage()));

        String content = templateEngine.render("tests/" + className.toLowerCase() + ".mustache", context);

        fileWriterService.write(outputDir, entity+className + ".java", content);
    }

}
