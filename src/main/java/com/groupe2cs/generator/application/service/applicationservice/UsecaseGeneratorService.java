package com.groupe2cs.generator.application.service.applicationservice;

import com.groupe2cs.generator.application.dto.SharedTemplate;
import com.groupe2cs.generator.domain.engine.FieldTransformer;
import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UsecaseGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public UsecaseGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {

      //  String outputShared = Utils.getRootDir(baseDir, definition.getName()) + "/" + generatorProperties.getSharedPackage();
        String outputShared = Utils.getParent(baseDir) + "/" + generatorProperties.getSharedPackage();
        String outputApplicationUseCase = baseDir + "/" + generatorProperties.getApplicationUseCasePackage();

        List<SharedTemplate> sharedTemplates = List.of(
                new SharedTemplate(
                        definition.getName() + "CreateApplicationService",
                        "application/createApplicationService.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getInfrastructurePackage()) + ".*"
                        ),
                        outputApplicationUseCase
                ),
                new SharedTemplate(
                        definition.getName() + "UpdateApplicationService",
                        "application/updateApplicationService.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) + ".*",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getInfrastructurePackage()) + ".*"
                        ),
                        outputApplicationUseCase
                ),
                new SharedTemplate(
                        definition.getName() + "ReadApplicationService",
                        "application/readApplicationService.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) + ".*",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getInfrastructurePackage()) + ".*"
                        ),
                        outputApplicationUseCase
                ),
                new SharedTemplate(
                        definition.getName() + "DeleteApplicationService",
                        "application/deleteApplicationService.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getQueryPackage()) + ".*",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getInfrastructurePackage()) + ".*"
                        ),
                        outputApplicationUseCase
                ),
                new SharedTemplate(
                        definition.getName() + "Gate",
                        "infrastructure/security/gate.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*"
                        ),
                        outputApplicationUseCase
                )
        );

        sharedTemplates.forEach(template -> generateSharedFile(template, definition));
    }

    private void generateSharedFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();

        String outputDir = template.getOutput();

        context.put("package", Utils.getPackage(outputDir));

        context.put("imports", template.getImports());
        context.put("name", Utils.capitalize(definition.getName()));
        context.put("entity", Utils.capitalize(definition.getName()));
        context.put("className", template.getClassName());

        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
        context.put("allFields", FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName()));
        context.put("fieldFiles", FieldTransformer.transform(definition.getFieldFiles(), definition.getName()));
        context.put("hasFiles", !definition.getFieldFiles().isEmpty());
        context.put("searchFields", FieldTransformer.transform(definition.searchFields(), definition.getName()));

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + ".java", content);
    }

}

