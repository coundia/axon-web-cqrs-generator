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
public class DtoRequestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public DtoRequestGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {
        Map<String, Object> context = new HashMap<>(definition.toMap());

        String outputDir = baseDir + "/" + generatorProperties.getDtoPackage();
        context.put("package", Utils.getPackage(outputDir));

        var fields = definition.getFields();
        context.put("fields", FieldTransformer.transform(fields, definition.getName()));
        context.put("editableFields", FieldTransformer.transform(definition.getEditableFields(), definition.getName()));

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*");
        imports.add("java.util.UUID");
        context.put("imports", imports);

        var fieldFiles = definition.getFieldFiles();
        context.put("hasFiles", !fieldFiles.isEmpty());
        if (!fieldFiles.isEmpty()) {
            context.put("fieldFiles", FieldTransformer.transform(fieldFiles, definition.getName()));
        }
        context.put("isMultiTenant", definition.getMultiTenant());
        String content = templateEngine.render("application/dtoRequest.mustache", context);
        fileWriterService.write(outputDir, definition.getName() + "Request.java", content);
    }
}
