package com.groupe2cs.generator.application.service.infrastructureservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FindByFieldRepositoryGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public FindByFieldRepositoryGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        String outputDir = baseDir + "/" + generatorProperties.getRepositoryPackage();
        String packageName = Utils.getPackage(outputDir);

        var fields = definition.getFields().stream().filter(p -> p.isFilable()).toList();

        Map<String, Object> context = new HashMap<>();
        context.put("package", packageName);
        context.put("name", definition.getName());
        context.put("className", definition.getName() + "Repository");
        context.put("fields", fields);
        context.put("dtoPackage", Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()));

        String content = templateEngine.render("infrastructure/findByFieldRepository.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Repository.java", content);
    }
}

