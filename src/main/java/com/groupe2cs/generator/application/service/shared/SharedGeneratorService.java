package com.groupe2cs.generator.application.service.shared;

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
public class SharedGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public SharedGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(EntityDefinition definition, String baseDir) {

        String outputShared = Utils.getRootDir(baseDir, definition.getName()) + "/" + generatorProperties.getSharedPackage();
        String outputApplicationUseCase = baseDir + "/" + generatorProperties.getApplicationUseCasePackage();

        List<SharedTemplate> sharedTemplates = List.of(
                new SharedTemplate(
                        "FileStorageService",
                        "shared/fileStorageService.mustache",
                        Set.of(
                                "org.springframework.web.multipart.MultipartFile"
                        ),
                        outputShared+"/"+generatorProperties.getInfrastructurePackage()
                )
                ,
                new SharedTemplate(
                        "FileStorageServiceImpl",
                        "shared/fileStorageServiceImpl.mustache",
                        Set.of(
                                "org.springframework.stereotype.Service",
                                "org.springframework.web.multipart.MultipartFile",
                                "java.io.IOException",
                                "java.nio.file.Files",
                                "java.nio.file.Path",
                                "java.nio.file.Paths",
                                "java.util.UUID"
                        ),
                        outputShared+"/"+generatorProperties.getInfrastructurePackage()
                        ),

                new SharedTemplate(
                        definition.getName() + "ApplicationService",
                        "application/applicationService.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage())+".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage())+".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage())+".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage())+".*",
                                Utils.getPackage(outputShared+"/"+generatorProperties.getInfrastructurePackage())+".*"
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

        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
        context.put("fieldFiles", FieldTransformer.transform(definition.fieldFiles(), definition.getName()));

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + ".java", content);
    }

}

