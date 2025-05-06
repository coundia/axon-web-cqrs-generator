package com.groupe2cs.generator.application.service.infrastructureservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RepositoryGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public RepositoryGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getRepositoryPackage();
        context.put("package", Utils.getPackage(outputDir));

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + "."+definition.getEntity());

        if(definition.getMultiTenant()) {
            imports.add(Utils.getPackage(Utils.getParent(baseDir) +
                    "/tenant/" +
                    generatorProperties.getEntityPackage()) + ".Tenant");
        }
        imports.add(Utils.getPackage(Utils.getParent(baseDir) + "/security/" + generatorProperties.getEntityPackage()) + ".User");

        context.put("imports", imports);

        String className = Utils.capitalize(definition.getName()) + "Repository";

        context.put("tableName", definition.getTable());
        context.put("entityName", definition.getEntity());
        context.put("aggregateName", definition.getName());
        context.put("name", definition.getName());
        context.put("className", className);
        var fields = definition.getFieldsWithoutRelations();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));

        var compositeKeys = definition.getFields().stream()
                .filter(f -> f.getRelation()!= null)
                .toList();


        if (compositeKeys.size() == 2) {
            Map<String, Object> left = new HashMap<>();
            left.put("name", compositeKeys.get(0).getName());
            left.put("nameCapitalized", Utils.capitalize(compositeKeys.get(0).getName()));
            left.put("type", compositeKeys.get(0).getType());

            Map<String, Object> right = new HashMap<>();
            right.put("name", compositeKeys.get(1).getName());
            right.put("nameCapitalized", Utils.capitalize(compositeKeys.get(1).getName()));
            right.put("type", compositeKeys.get(1).getType());

            Map<String, Object> composite = new HashMap<>();
            composite.put("left", left);
            composite.put("right", right);

            context.put("compositeKeys", List.of(composite));
        }

        context.put("isMultiTenant", definition.getMultiTenant());

        String content = templateEngine.render("infrastructure/repository.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Repository.java", content);
    }
}
