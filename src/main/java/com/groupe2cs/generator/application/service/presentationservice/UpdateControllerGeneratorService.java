package com.groupe2cs.generator.application.service.presentationservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
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
public class UpdateControllerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public UpdateControllerGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getControllerPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("nameLowercase", Utils.unCapitalize(definition.getName()));

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()+".*"));
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getApplicationUseCasePackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*");

        String sharedDir = Utils.getParent(baseDir)+"/"+generatorProperties.getSharedPackage();
        imports.add(Utils.getPackage(sharedDir + "/" + generatorProperties.getInfrastructurePackage()) + ".audit.RequestContext");
        imports.add(Utils.getPackage(sharedDir + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest");


        context.put("imports", imports);

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));
        context.put("fieldFiles", FieldTransformer.transform(definition.getFieldFiles(), definition.getName()));

        Boolean hasFiles = !definition.getFieldFiles().isEmpty();
        context.put("hasFiles",hasFiles);
        context.put("security", definition.isInStack("security"));
        context.put("isMultiTenant", definition.getMultiTenant());

        if(!hasFiles){
            String content = templateEngine.render("presentation/updateController.mustache", context);
            fileWriterService.write(outputDir, "Update" + definition.getName() + "Controller.java", content);
            return;
        }
        context.put("nameUpperCase", definition.getName().toUpperCase());
        String content = templateEngine.render("presentation/updateWithFilesController.mustache", context);
        fileWriterService.write(outputDir, "Update" + definition.getName() + "Controller.java", content);

    }
}
