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
public class ControllerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public ControllerGeneratorService(
            TemplateEngine templateEngine,
            FileWriterService fileWriterService,
            GeneratorProperties generatorProperties
    ) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String outputDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        outputDir = outputDir + "/" + generatorProperties.getControllerPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("dtoName", definition.getName() + "Dto");
        context.put("mapperName", definition.getName() + "Mapper");
        context.put("aggregateName", definition.getName() + "Aggregate");
        context.put("lowerName", Utils.lowerFirst(definition.getName()));

        var idField = definition.getFields().stream()
                .filter(f -> f.getName().equalsIgnoreCase("id"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No ID field found"));
        context.put("idType", idField.getType());

        String content = templateEngine.render("presentation/controller.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Controller.java", content);
    }
}
