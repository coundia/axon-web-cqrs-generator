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
public class DomainTestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public DomainTestGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {

        List<SharedTemplate> testTemplates = List.of(
                new SharedTemplate(
                        definition.getName() + "AggregateTests",
                        "tests/AggregateTests.mustache",
                        Set.of(
                                Utils.getTestPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
                                Utils.getTestPackage(Utils.getParent(baseDir) + "/" + generatorProperties.getSharedPackage()) + ".*",
                                Utils.getTestPackage(baseDir + "/" + generatorProperties.getExceptionPackage()) + ".*",
                                "org.junit.jupiter.api.Test",
                                "static org.assertj.core.api.Assertions.assertThat",
                                "java.util.UUID",
                                "static org.junit.jupiter.api.Assertions.assertThrows"
                        ),
                        baseDir
                )
        );

        testTemplates.forEach(template -> generateDomainTestFile(template, definition));
    }

    private void generateDomainTestFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();


        String fullPath = template.getOutput() + "/" + generatorProperties.getDomainPackage();
        String packageName = Utils.getTestPackage(fullPath);
        String outputDir = Utils.getTestDir(fullPath);

        String className = template.getClassName();
        String aggregateName = definition.getName() + "Aggregate";
        String lowerName = Character.toLowerCase(definition.getName().charAt(0)) + definition.getName().substring(1);

        context.put("package", packageName);
        context.put("className", className);
        context.put("aggregateName", aggregateName);
        context.put("lowerName", lowerName);

        var fields = FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName());
        context.put("fields", fields);

        context.put("imports", template.getImports());

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, className + ".java", content);
    }
}
