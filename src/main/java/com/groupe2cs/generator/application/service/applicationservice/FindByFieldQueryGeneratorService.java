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
public class FindByFieldQueryGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public FindByFieldQueryGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        String outputDir = baseDir + "/" + generatorProperties.getQueryPackage();
        String packageName = Utils.getPackage(outputDir);

        var fields = definition.getFields().stream().filter(p -> p.isFilable()).toList();

        for (var field : fields) {
            field.setNameCapitalized(capitalize(field.getName()));

            Map<String, Object> context = new HashMap<>();
            context.put("package", packageName);
            context.put("field", field);
            context.put("name", definition.getName());

            String className = "FindBy" + field.getNameCapitalized() + definition.getName() + "Query";
            context.put("className", className);

            Set<String> imports = new LinkedHashSet<>();
            imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");

            context.put("imports",imports);

            String content = templateEngine.render("application/findByFieldQuery.mustache", context);
            fileWriterService.write(outputDir, className + ".java", content);
        }
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
