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
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + "." + definition.getName());

        context.put("imports", imports);

        context.put("tableName", definition.getTable());
        context.put("entityName", definition.getName());
        var fields = definition.getFields();
        context.put("fields", FieldTransformer.realField(fields, definition.getName()));

        var filables = definition.getFields().stream().filter(p -> p.isFilable()).toList();

        context.put("filables", filables);

        String content = templateEngine.render("infrastructure/repository.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Repository.java", content);
    }
}
