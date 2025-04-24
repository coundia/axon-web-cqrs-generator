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
        context.put("nameLowercase", Utils.unCapitalize(definition.getName()));
        context.put("nameUpperCase", definition.getName().toUpperCase());
        context.put("dtoPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()));

        var fields = definition.getFields();
        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getApplicationUseCasePackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*");

        String sharedDir = Utils.getParent(baseDir)+"/"+generatorProperties.getSharedPackage();
        imports.add(Utils.getPackage(sharedDir + "/" + generatorProperties.getInfrastructurePackage()) + ".audit.RequestContext");


        context.put("imports", imports);
        context.put("security", definition.isInStack("security"));

        context.put("fields", FieldTransformer.transform(fields, definition.getName()));
        String content = templateEngine.render("presentation/createController.mustache", context);

        var fieldFiles = definition.getFieldFiles();
        context.put("hasFiles", !fieldFiles.isEmpty());
        if (!fieldFiles.isEmpty()) {
            Set<String> importsBis = new LinkedHashSet<>();
            importsBis.add(Utils.getPackage(baseDir + "/" + generatorProperties.getApplicationUseCasePackage()) + ".*");
            importsBis.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
            context.put("imports", importsBis);
            context.put("fieldFiles", FieldTransformer.transform(fieldFiles, definition.getName()));
            content = templateEngine.render("presentation/createWithFilesController.mustache", context);
        }

        fileWriterService.write(outputDir, "Add" + definition.getName() + "Controller.java", content);
    }
}
