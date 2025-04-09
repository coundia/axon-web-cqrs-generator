package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ListQueryGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public ListQueryGeneratorService(
            TemplateEngine templateEngine,
            FileWriterService fileWriterService,
            GeneratorProperties generatorProperties
    ) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        String outputDir = baseDir + "/" + generatorProperties.getQueryPackage();

        Map<String, Object> context = new HashMap<>();
        context.put("package", Utils.getPackage(outputDir));
        context.put("name", definition.getName());

        String content = templateEngine.render("application/list-query.mustache", context);
        fileWriterService.write(outputDir, "List" + definition.getName() + "Query.java", content);
    }
}
