package com.groupe2cs.generator.application.service.presentationservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class DeleteControllerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public DeleteControllerGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
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
        context.put("commandPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()));

        context.put("isMultiTenant", definition.getMultiTenant());
        context.put("apiPrefix", definition.getApiPrefix());

        context.put("security", definition.isInStack("security"));
        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getExceptionPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getApplicationUseCasePackage()) + ".*");

        String sharedDir = Utils.getParent(baseDir)+"/"+generatorProperties.getSharedPackage();
        imports.add(Utils.getPackage(sharedDir + "/" + generatorProperties.getInfrastructurePackage()) + ".audit.RequestContext");
        imports.add(Utils.getPackage(sharedDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest");

        context.put("imports", imports);

        String content = templateEngine.render("presentation/deleteController.mustache", context);
        fileWriterService.write(outputDir, "Delete" + definition.getName() + "Controller.java", content);
    }
}
