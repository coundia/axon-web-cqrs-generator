package com.groupe2cs.generator.application.service.infrastructureservice;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RabbitMqGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public RabbitMqGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {

        String sharedInfra = Utils.getRootDir(baseDir, definition.getName()) + "/" + generatorProperties.getSharedPackage();
        String outputInfra = baseDir + "/" + generatorProperties.getInfrastructurePackage();

        List<SharedTemplate> templates = List.of(
                new SharedTemplate(
                        Utils.capitalize(definition.getName()) + "EventConsumer",
                        "infrastructure/rabbitMq/rabbitMqEventConsumer.mustache",
                        Set.of(
                                Utils.getPackage(Utils.getParent(baseDir) + "/security/" + generatorProperties.getEntityPackage()) + ".User",
                                Utils.getPackage(Utils.getParent(baseDir) + "/tenant/" + generatorProperties.getEntityPackage()) + ".Tenant",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + "." + definition.getName(),
                                Utils.getPackage(baseDir + "/" + generatorProperties.getEventPackage()) + ".*"
                        ),
                        outputInfra + "/rabbitMq"
                ),
                new SharedTemplate(
                        Utils.capitalize(definition.getName()) + "EventPublisher",
                        "infrastructure/rabbitMq/rabbitMqEventPublisher.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getEventPackage()) + ".*"
                        ),
                        outputInfra + "/rabbitMq"
                ),
                new SharedTemplate(
                        Utils.capitalize(definition.getName()) + "RabbitMqConfig",
                        "infrastructure/rabbitMq/rabbitMqEntityConfig.mustache",
                        null,
                        outputInfra + "/rabbitMq"
                ),
                new SharedTemplate(
                        "RabbitMqConfig",
                        "infrastructure/rabbitMq/rabbitMqSharedConfig.mustache",
                        null,
                        sharedInfra + "/infrastructure/rabbitMq"
                )
        );

        templates.forEach(template -> generateFile(template, definition));
    }

    private void generateFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();
        String outputDir = template.getOutput();

        context.put("package", Utils.getPackage(outputDir));
        context.put("imports", template.getImports());
        context.put("entity", Utils.capitalize(definition.getName()));
        context.put("entityLowerCase", Utils.lowerCase(definition.getName()));
        context.put("className", template.getClassName());
        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + ".java", content);
    }
}
