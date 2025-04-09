package com.groupe2cs.generator.application.service.presentationservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CreateControllerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public CreateControllerGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getControllerPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("nameLowercase", definition.getName().toLowerCase());
        context.put("mapperPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()));
        context.put("dtoPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()));
        context.put("commandPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()));

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        String content = templateEngine.render("presentation/createController.mustache", context);
        fileWriterService.write(outputDir, "Add" + definition.getName() + "Controller.java", content);
    }
}
