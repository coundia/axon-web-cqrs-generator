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
public class ProjectionGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public ProjectionGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getProjectionPackage();
        context.put("package", Utils.getPackage(outputDir));

        String rootDir = Utils.getParent(baseDir);

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEventPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*");

        imports.add(Utils.getPackage(rootDir + "/security/" + generatorProperties.getEntityPackage()) + ".CustomUser");

        if(definition.getMultiTenant()){
            imports.add(Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getEntityPackage()) + ".Tenant");
        }

        imports.add("org.axonframework.eventhandling.EventHandler");
        context.put("security", definition.isInStack("security"));

        context.put("nameAggregate", definition.getName());
        context.put("entity", definition.getEntity());
        context.put("name", definition.getName());

        context.put("imports", imports);
        var fields = definition.getFieldsWithRelations();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));
        context.put("fieldWithoutRelations", FieldTransformer.transform(definition.getFieldsWithoutRelations(), definition.getName()));
        context.put("editableFields", FieldTransformer.transform(definition.getEditableFields(), definition.getName()));
        context.put("nameUpperCase", definition.getName().toUpperCase());
        context.put("isMultiTenant", definition.getMultiTenant());
        String content = templateEngine.render("presentation/projection.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Projection.java", content);
    }
}
