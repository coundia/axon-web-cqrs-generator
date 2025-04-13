package com.groupe2cs.generator.application.service.testservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class AllTestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;
    private final DomainTestGeneratorService domainTestGeneratorService;
    private final CommandApplicationTestGeneratorService commandApplicationTestGeneratorService;

    public void generate(EntityDefinition definition, String baseDir) {

        domainTestGeneratorService.generate(definition,baseDir);
        commandApplicationTestGeneratorService.generate(definition,baseDir);

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

        var fieldFiles = definition.getFieldFiles();
        context.put("hasFiles", !fieldFiles.isEmpty());
        context.put("fieldFiles", FieldTransformer.transform(fieldFiles, definition.getName()));

        if (!fieldFiles.isEmpty() && className.equalsIgnoreCase("CreateControllerIntegrationTest")) {

            String content = templateEngine.render("tests/createWithFilesControllerIntegrationTest.mustache", context);
            fileWriterService.write(outputDir, entity+className + ".java", content);
            return;
        }

        String content = templateEngine.render("tests/" + className.toLowerCase() + ".mustache", context);
        fileWriterService.write(outputDir, entity+className + ".java", content);
    }

}
