package com.groupe2cs.generator.application.service.applicationservice;

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

        String outputShared = Utils.getParent(baseDir) + "/" + generatorProperties.getSharedPackage();
        String outputSecurity = Utils.getParent(baseDir) + "/" + generatorProperties.getSecurityPackage();
        String outputApplicationUseCase = baseDir + "/" + generatorProperties.getApplicationUseCasePackage();

        List<SharedTemplate> sharedTemplates = new ArrayList<>();
        sharedTemplates.addAll(List.of(
                new SharedTemplate(
                        definition.getName() + "CreateApplicationService",
                        "application/createApplicationService.mustache",
                        Set.of(
                                Utils.getPackage(baseDir + "/" + generatorProperties.getMapperPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getDtoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getVoPackage()) + ".*",
                                Utils.getPackage(baseDir + "/" + generatorProperties.getCommandPackage()) + ".*",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
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
                                Utils.getPackage(outputShared + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
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
                                Utils.getPackage(outputShared + "/" + generatorProperties.getInfrastructurePackage()) + ".*",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest"

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
                                Utils.getPackage(outputShared + "/" + generatorProperties.getDtoPackage()) + ".MetaRequest",
                                Utils.getPackage(outputShared + "/" + generatorProperties.getInfrastructurePackage()) + ".*"
                        ),
                        outputApplicationUseCase
                )
        ));
        List <SharedTemplate> sharedTestsTemplates = new ArrayList<>();

        Set<String> importsGate = new HashSet<>( Set.of(
                Utils.getPackage(baseDir + "/" + generatorProperties.getRepositoryPackage()) + ".*",
                Utils.getPackage(outputSecurity + "/" + generatorProperties.getServicePackage()) + ".UserPrincipal",
                Utils.getPackage(outputSecurity + "/" + generatorProperties.getEntityPackage()) + ".User",
                Utils.getPackage(Utils.getParent(baseDir) + "/tenant/" + generatorProperties.getEntityPackage()) + ".Tenant",
                Utils.getPackage(Utils.getParent(baseDir) + "/security/" + generatorProperties.getServicePackage()) + ".JwtService"

        ));

        if(
                !definition.getEntity().equalsIgnoreCase("User") &&
                !definition.getEntity().equalsIgnoreCase("Tenant") &&
                !definition.getEntity().equalsIgnoreCase("User")

        ){
            importsGate.add(Utils.getPackage(baseDir + "/" + generatorProperties.getEntityPackage()) + "."+definition.getEntity());
        }

        if (definition.isInStack("security")) {
            sharedTemplates.add(
                    new SharedTemplate(
                            definition.getName() + "Gate",
                            "infrastructure/security/gate.mustache",
                            importsGate,
                            outputApplicationUseCase
                    )
            );

            sharedTestsTemplates.add(
                    new SharedTemplate(
                            definition.getName() + "GateTests",
                            "infrastructure/security/gateTests.mustache",
                            importsGate,
                            outputApplicationUseCase
                    )
            );

        }

        sharedTemplates.forEach(template -> generateSharedFile(template, definition));
        sharedTestsTemplates.forEach(template -> generateSharedTestsFile(template, definition));
    }

    private void generateSharedFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();

        String outputDir = template.getOutput();

        context.put("package", Utils.getPackage(outputDir));

        context.put("imports", template.getImports());
        context.put("nameAggregate", Utils.capitalize(definition.getName()));
        context.put("name", Utils.capitalize(definition.getName()));
        context.put("entity", Utils.capitalize(definition.getEntity()));
        context.put("className", template.getClassName());

        context.put("entityLowerCase", Utils.unCapitalize(definition.getEntity()));

        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
        context.put("allFields", FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName()));
        context.put("fieldFiles", FieldTransformer.transform(definition.getFieldFiles(), definition.getName()));
        context.put("hasFiles", !definition.getFieldFiles().isEmpty());
        context.put("searchFields", FieldTransformer.transform(definition.searchFields(), definition.getName()));

        context.put("editableFields", FieldTransformer.transform(definition.getEditableFields(), definition.getName()));

        context.put("isMultiTenant", definition.getMultiTenant());

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + ".java", content);
    }

    private void generateSharedTestsFile(SharedTemplate template, EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();

        String outputDir = Utils.getTestDir(template.getOutput());

        context.put("package", Utils.getPackage(template.getOutput()));

        context.put("imports", template.getImports());
        context.put("name", Utils.capitalize(definition.getName()));
        context.put("nameAggregate", Utils.capitalize(definition.getName()));
        context.put("entity", Utils.capitalize(definition.getEntity()));
        context.put("entityLowerCase", Utils.unCapitalize(definition.getEntity()));

        context.put("className", template.getClassName());

        context.put("fields", FieldTransformer.transform(definition.getFields(), definition.getName()));
        context.put("allFields", FieldTransformer.transform(definition.getAllFieldsWithoutOneToMany(), definition.getName()));
        context.put("fieldFiles", FieldTransformer.transform(definition.getFieldFiles(), definition.getName()));
        context.put("hasFiles", !definition.getFieldFiles().isEmpty());
        context.put("searchFields", FieldTransformer.transform(definition.searchFields(), definition.getName()));


        context.put("isMultiTenant", definition.getMultiTenant() );

        String content = templateEngine.render(template.getTemplatePath(), context);
        fileWriterService.write(outputDir, template.getClassName() + ".java", content);
    }

}

