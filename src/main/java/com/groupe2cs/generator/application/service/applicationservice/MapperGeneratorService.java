package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MapperGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public MapperGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getMapperPackage();
        context.put("package", Utils.getPackage(outputDir));

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));
        context.put("entity", definition.getName());

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getDomainPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + ".*");
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*");

        context.put("imports", imports);

        String content = templateEngine.render("application/mapper.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Mapper.java", content);
    }
}
