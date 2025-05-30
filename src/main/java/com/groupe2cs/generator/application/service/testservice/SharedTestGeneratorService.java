package com.groupe2cs.generator.application.service.testservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.domain.model.EntityDefinition;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SharedTestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public SharedTestGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(String baseDir, EntityDefinition definition) {

        if("Security".equalsIgnoreCase(definition.getModule())){
            return;
        }

        baseDir = Utils.getParent(baseDir);

        generateBaseIntegrationTests(baseDir,definition);
        generateBaseUnitTests(baseDir);
        generateControllerTest(baseDir);
    }

    private void generateBaseIntegrationTests(String baseDir,EntityDefinition definition) {
        Map<String, Object> context = new HashMap<>();

        String fullPath = baseDir + "/" + generatorProperties.getSharedPackage();

        String packageName = Utils.getTestPackage(fullPath);
        context.put("package", packageName);
        String outputDir = Utils.getTestDir(fullPath);

        context.put("apiPrefix", definition.getApiPrefix());

        Set<String> imports = new LinkedHashSet<>();
        if(definition.isInStack("security")) {
            imports.add(Utils.getTestPackage(baseDir+ "/" +generatorProperties.getSecurityPackage()+"/"+generatorProperties.getDtoPackage()) + ".*");
            imports.add(Utils.getTestPackage(baseDir+ "/" +generatorProperties.getSecurityPackage()+"/"+generatorProperties.getEntityPackage()) + ".User");
            imports.add(Utils.getTestPackage(baseDir+ "/tenant/"+generatorProperties.getEntityPackage()) + ".Tenant");
        }

        context.put("imports", imports);
        context.put("security", definition.isInStack("security"));

        String content = templateEngine.render("tests/baseIntegrationTest.mustache", context);
        fileWriterService.write(outputDir, "BaseIntegrationTests.java", content);
    }

    private void generateBaseUnitTests(String baseDir) {
        Map<String, Object> context = new HashMap<>();

        String fullPath = baseDir + "/" + generatorProperties.getSharedPackage();

        String packageName = Utils.getTestPackage(fullPath);
        context.put("package", packageName);
        String outputDir = Utils.getTestDir(fullPath);

        String content = templateEngine.render("tests/baseUnitTest.mustache", context);
        fileWriterService.write(outputDir, "BaseUnitTests.java", content);
    }

    private void generateControllerTest(String baseDir) {
        Map<String, Object> context = new HashMap<>();
        String fullPath = baseDir + "/" + generatorProperties.getControllerPackage();

        String packageName = Utils.getTestPackage(fullPath);
        context.put("package", packageName);
        String outputDir = Utils.getTestDir(fullPath);

        context.put("testAction","status");
        context.put("endpoint","/v1/status");
        context.put("className","StatusControllerTest");

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getTestPackage(baseDir+ "/" + generatorProperties.getSharedPackage()) + ".*");
        imports.add("java.util.UUID");
        context.put("imports", imports);

        String content = templateEngine.render("tests/controllerIntegrationTest.mustache", context);

        fileWriterService.write(outputDir,  "StatusControllerTest.java", content);
    }

}
