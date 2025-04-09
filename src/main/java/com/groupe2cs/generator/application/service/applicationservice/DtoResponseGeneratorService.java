package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DtoResponseGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public DtoResponseGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getDtoPackage();
        context.put("package", Utils.getPackage(outputDir));

        var fields = definition.getFields();
        context.put("fields", fields);

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*");
        context.put("imports", imports);

        String content = templateEngine.render("application/dtoResponse.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Response.java", content);
    }
}
