package com.groupe2cs.generator.application.usecase;

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

        String rootDir = Utils.getRootDir(baseDir, definition.getName());

        String outputShared = Utils.getRootDir(baseDir, definition.getName()) + "/" + generatorProperties.getSharedPackage();

        List<SharedTemplate> sharedTemplates = List.of(
                new SharedTemplate(
                        "StatusController",
                        "shared/status/statusController.mustache",
                        Set.of(
                                Utils.getPackage(rootDir + "/shared/" + generatorProperties.getApplicationPackage()) + ".ApiResponseDto"
                        ),
                        outputShared + "/" + generatorProperties.getPresentationPackage()+"/status"
                ),
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
                ,
                new SharedTemplate(
                        "AbstractAuditableEntity",
                        "shared/audit/abstractAuditableEntity.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+"/audit"
                ),

                new SharedTemplate(
                        "Auditable",
                        "shared/audit/auditable.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+"/audit"
                ),

                new SharedTemplate(
                        "RequestContext",
                        "shared/audit/requestContext.mustache",
                        Set.of(
                                Utils.getPackage(rootDir + "/security/" + generatorProperties.getServicePackage()) + ".UserPrincipal"
                        ),
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+"/audit"
                ),

                new SharedTemplate(
                        "IdentifiableUser",
                        "shared/audit/identifiableUser.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+"/audit"
                ),


                new SharedTemplate(
                        "AuditListener",
                        "shared/audit/auditListener.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+"/audit"
                ),

                new SharedTemplate(
                        "MetaRequest",
                        "shared/dto/metaRequest.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getDtoPackage()
                )
                ,
                new SharedTemplate(
                        "FlywayConfig",
                        "shared/flyway/flywayConfig.mustache",
                        null,
                        outputShared + "/" + generatorProperties.getInfrastructurePackage()+"/flyway"
                )
                ,
                new SharedTemplate(
                        "V1__insert_security_defaults",
                        "shared/flyway/V1__insert_security_defaults.mustache",
                        null,
                        Utils.getSrcDir(baseDir) +"/main/resources/db/migration",
                        null,
                        ".sql"
                )


        );

        sharedTemplates.forEach(template -> generateSharedFile(template, definition));

        //tests
        List<SharedTemplate> testTemplates = List.of(
                new SharedTemplate(
                          "StatusControllerTest",
                        "shared/status/statusControllerTest.mustache",
                        Set.of(
                                Utils.getPackage(rootDir + "/shared/" + generatorProperties.getApplicationPackage()) + ".ApiResponseDto",
                                Utils.getPackage(outputShared) + ".BaseUnitTests"
                        ),
                        outputShared + "/" + generatorProperties.getPresentationPackage()+"/status"
                )
        );

        testTemplates.forEach(template -> generateSharedTestFile(template, definition));
    }

    private void generateSharedFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();

        String outputDir = template.getOutput();

        context.put("package", Utils.getPackage(outputDir));

        context.put("imports", template.getImports());
        context.put("name", Utils.capitalize(definition.getName()));
        context.put("className", template.getClassName());
        context.put("entity", Utils.capitalize(definition.getEntity()));

        context.put("nameAggregate", definition.getName());

        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));

        context.put("isMultiTenant", definition.getMultiTenant());
        context.put("apiPrefix", definition.getApiPrefix());

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + template.getExt(), content);
    }


    private void generateSharedTestFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();


        String fullPath = template.getOutput();
        String packageName = Utils.getTestPackage(fullPath);
        String outputDir = Utils.getTestDir(fullPath);

        String className = template.getClassName();
        String aggregateName = definition.getName() + "Aggregate";
        String lowerName = Character.toLowerCase(definition.getName().charAt(0)) + definition.getName().substring(1);

        context.put("package", packageName);
        context.put("className", className);
        context.put("aggregateName", aggregateName);
        context.put("lowerName", lowerName);

        var fields = FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName());
        context.put("fields", fields);

        context.put("imports", template.getImports());

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, className + ".java", content);
    }

}

