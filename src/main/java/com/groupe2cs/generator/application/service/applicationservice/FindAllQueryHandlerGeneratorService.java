package com.groupe2cs.generator.application.service.applicationservice;

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
public class FindAllQueryHandlerGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public FindAllQueryHandlerGeneratorService(
            TemplateEngine templateEngine,
            FileWriterService fileWriterService,
            GeneratorProperties generatorProperties
    ) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>();

        String outputDir = baseDir + "/" + generatorProperties.getQueryHandlerPackage();
        context.put("package", Utils.getPackage(outputDir));
        context.put("entity", definition.getEntity());
        context.put("nameAggregate", definition.getName());
        context.put("name", definition.getName());
        context.put("entityLower", Utils.unCapitalize(definition.getName()));

        Set<String> imports = new LinkedHashSet<>();
        imports.add("org.axonframework.queryhandling.QueryHandler");
        imports.add("org.springframework.stereotype.Component");

        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*");

        context.put("imports", imports);

        String content = templateEngine.render("application/findAllQueryHandler.mustache", context);
        fileWriterService.write(outputDir, "FindAll" + definition.getName() + "QueryHandler.java", content);
    }
}
