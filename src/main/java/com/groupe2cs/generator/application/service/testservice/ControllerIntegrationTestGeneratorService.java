package com.groupe2cs.generator.application.service.testservice;

import com.groupe2cs.generator.domain.engine.FileWriterService;
import com.groupe2cs.generator.domain.engine.TemplateEngine;
import com.groupe2cs.generator.infrastructure.config.GeneratorProperties;
import com.groupe2cs.generator.shared.Utils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class ControllerIntegrationTestGeneratorService {

    private final TemplateEngine templateEngine;
    private final FileWriterService fileWriterService;
    private final GeneratorProperties generatorProperties;

    public ControllerIntegrationTestGeneratorService(TemplateEngine templateEngine, FileWriterService fileWriterService, GeneratorProperties generatorProperties) {
        this.templateEngine = templateEngine;
        this.fileWriterService = fileWriterService;
        this.generatorProperties = generatorProperties;
    }

    public void generate(String baseDir) {
        generateBaseIntegrationTests(baseDir);
        generateBaseUnitTests(baseDir);
        generateControllerTest(baseDir);
    }

    private void generateBaseIntegrationTests(String baseDir) {
        Map<String, Object> context = new HashMap<>();

        String fullPath = baseDir + "/" + generatorProperties.getSharedPackage();

        String packageName = Utils.getTestPackage(fullPath);
        context.put("package", packageName);
        String outputDir = Utils.getTestDir(fullPath);

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
        context.put("endpoint","/api/v1/status");
        context.put("className","StatusControllerTest");

        Set<String> imports = new LinkedHashSet<>();
        imports.add(Utils.getTestPackage(baseDir + "/" + generatorProperties.getSharedPackage()) + ".*");
        context.put("imports", imports);

        String content = templateEngine.render("tests/controllerIntegrationTest.mustache", context);

        fileWriterService.write(outputDir,  "StatusControllerTest.java", content);
    }

}
