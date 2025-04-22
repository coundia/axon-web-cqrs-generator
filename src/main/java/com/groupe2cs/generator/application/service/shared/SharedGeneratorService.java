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

        if(
                "Security".equalsIgnoreCase(definition.getModule())
        ){
            return;
        }

        String outputShared = Utils.getRootDir(baseDir, definition.getName()) + "/" + generatorProperties.getSharedPackage();

        List<SharedTemplate> sharedTemplates = List.of(
                new SharedTemplate(
                        "ApiResponseDto",
                        "shared/apiResponseDto.mustache",
                        Set.of("lombok.Builder"),
                        outputShared + "/" + generatorProperties.getApplicationPackage()
                ),
                new SharedTemplate(
                        "FileStorageService",
                        "shared/fileStorageService.mustache",
                        Set.of(
                                "org.springframework.web.multipart.MultipartFile"
                        ),
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()
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
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()
                )
                ,
                new SharedTemplate(
                        "RetryTwoTimesHandler",
                        "shared/retryTwoTimesHandler.mustache",
                        null,
                        outputShared + "/" +generatorProperties.getInfrastructurePackage()+ "/axon"
                )
                ,
                new SharedTemplate(
                        "AxonConfig",
                        "shared/axonConfig.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+ "/axon"
                )
                ,
                new SharedTemplate(
                        "AxonNotificationErrorHandler",
                        "shared/AxonNotificationErrorHandler.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+ "/axon"
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
        context.put("className", template.getClassName());
        context.put("entity", Utils.capitalize(definition.getName()));

        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + ".java", content);
    }

}

