package com.groupe2cs.generator.application.service.presentationservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FindByFieldProjectionGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public FindByFieldProjectionGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        String outputDir = baseDir + "/" + generatorProperties.getProjectionPackage();
        String packageName = Utils.getPackage(outputDir);

        var fields = definition.getFields().stream().filter(p -> p.isFilable()).toList();

        for (var field : fields) {
            field.setNameCapitalized(capitalize(field.getName()));

            Map<String, Object> context = new HashMap<>();
            context.put("package", packageName);
            context.put("field", field);
            context.put("name", definition.getName());
            context.put("repositoryPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()));
            context.put("queryPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()));
            context.put("dtoPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()));

            String className = "FindBy" + field.getNameCapitalized() + definition.getName() + "Projection";
            context.put("className", className);

            String content = templateEngine.render("presentation/findByFieldProjection.mustache", context);
            fileWriterService.write(outputDir, className + ".java", content);
        }
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
