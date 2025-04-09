package com.groupe2cs.generator.application.service.domainservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.domain.model.FieldDefinition;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class VoGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;


    public VoGeneratorService(
            TemplateEngine templateEngine,
            FileWriterService fileWriterService,
            GeneratorProperties generatorProperties
    ) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {

        String outputDir = baseDir +"/" + this.generatorProperties.getVoPackage();
        Set<String> imports = new LinkedHashSet<>();
        for (FieldDefinition field : definition.getFields()) {
            String voName = definition.getName() + Utils.capitalize(field.getName());

            Map<String, Object> context = new HashMap<>(definition.toMap());

            context.put("package", Utils.getPackage(outputDir));
            context.put("voName", voName);
            context.put("type", field.getType());
            context.put("name", field.getName());
            context.put("equalsExpression", "this." + field.getName() + ".equals(that." + field.getName() + ")");
            context.put("hashCodeExpression", "java.util.Objects.hash("+field.getName()+")");

            if (field.isDate()) {
                imports.add("java.util.Date");
                imports.add("com.fasterxml.jackson.annotation.JsonCreator");
                imports.add("com.fasterxml.jackson.annotation.JsonProperty");
            }
            context.put("imports", imports);
            String voCode = templateEngine.render("domain/vo.mustache", context);

            fileWriterService.write(outputDir, voName + ".java", voCode);
        }
    }

}
