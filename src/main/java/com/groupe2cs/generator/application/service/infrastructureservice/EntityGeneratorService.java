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
public class EntityGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public EntityGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String rootDir = Utils.getParent(baseDir);

        String outputDir = baseDir + "/" + generatorProperties.getEntityPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("base", Utils.getPackage(rootDir));
        context.put("tableName", definition.getTable());
        context.put("entity", definition.getName());
        context.put("entityLowerCase", Utils.unCapitalize(definition.getName()));


        context.put("fields", FieldTransformer.transform(definition.getFieldsWithRelations(), definition.getName()));
        context.put("isAuditable", definition.getAuditable());

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(rootDir + "/security/" + generatorProperties.getEntityPackage()) + ".User");

        if(definition.isInStack("isMultiTenant")){
            imports.add(Utils.getPackage(rootDir + "/tenant/" + generatorProperties.getEntityPackage()) + ".Tenant");
        }

        if (definition.hasRelation("oneToMany")) {
            imports.add("jakarta.persistence.OneToMany");
            imports.add("java.util.List");
            imports.add("java.util.ArrayList");
        }

        if (definition.hasRelation("manyToOne")) {
            imports.add("jakarta.persistence.ManyToOne");
            imports.add("jakarta.persistence.JoinColumn");
        }

        context.put("imports", imports);

        String content = templateEngine.render("infrastructure/entity.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + ".java", content);
    }
}
